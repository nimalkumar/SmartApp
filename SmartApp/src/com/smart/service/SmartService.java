package com.smart.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import com.smart.service.util.SmartServiceUtil;
import com.smart.to.RequestTO;
import com.smart.to.ResultTO;
import com.smart.util.to.RouteInfoTO;
import com.smart.util.to.TrafficInfoTO;
import com.smart.util.to.VehicleInfoTO;
import com.smart.util.to.VehicleLiveInfoTO;
import com.smart.util.to.WeatherInfoTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value="/")

@Path("/smartService") 
public class SmartService {  
	
	Logger logger = Logger.getGlobal();
	
	
	@POST 
   @Path("/getEta") 
	@Consumes(MediaType.APPLICATION_JSON) 
   @Produces(MediaType.APPLICATION_JSON) 
	@ApiOperation(value="Get ETA.", response=ResultTO.class)
	@ApiResponses({ @ApiResponse(code = 200, response = ResultTO.class, message = "Get ETA time") })
   public ResultTO getEta(RequestTO requestTO){
		
		ResultTO resultTO = new ResultTO();
		
		try {
			String routeTripStopKey = "";
			
			//get userinput and determine if it's a ticketnumber or routekey
			String userInput = requestTO.getUserInput();
			String ticketPattern = "^n[0-9]{4}[a-z]";
			String routeKeyPattern = "^[A-Z,a-z,0-9]{1,10}-[0-9]{1,2}-[0-9]";
			if (userInput.matches(ticketPattern))
			{
				//It's a ticket number entered
				routeTripStopKey = (String) SmartServiceUtil.getTicketMap().get(userInput.toLowerCase());
				
			}
			else if (userInput.matches(routeKeyPattern))
			{
				//It's a route Key given
				routeTripStopKey = userInput;
			}
			else
			{
				routeTripStopKey = "Not supported";
				logger.log(Level.ALL, "aNot supported at the moment:" + userInput);
				logger.log(Level.INFO, "iNot supported at the moment:" + userInput);
				logger.log(Level.SEVERE, "iNot supported at the moment:" + userInput);
				System.out.println("Not supported at the moment");
				throw new Exception("Request Failed. Please enter valid Ticket Number or Route Key.");
			}
			
			logger.log(Level.ALL, "arouteTripStopKey:" + routeTripStopKey);
			logger.log(Level.INFO, "irouteTripStopKey:" + routeTripStopKey);
			
			//get optional date info from request; Format expected "MM/dd/yyyy"
			String travelDateStr = requestTO.getTravelDate();
			Date travelDate;
			if (travelDateStr == null)
			{
				System.out.println("Date not specified and so using current date");
				travelDate = new Date();
			}
			else
			{
				try {
					SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
					travelDate = fmt.parse(travelDateStr);
				} catch (ParseException e) {
					
					System.out.println("Incorrect Date format, so using current date");
					travelDate = new Date();
				}
				
			}
			
			VehicleInfoTO vehicleInfoTO = constructVehicleInfoTO(routeTripStopKey, travelDate);
			WeatherInfoTO weatherInfoTO = constructWeatherInfoTO(travelDate);
			TrafficInfoTO trafficInfoTO = constructTrafficInfoTO(vehicleInfoTO, travelDate);
			
			//call arrival delay prediction service
			String requestJson = constructPredictServiceRequest(vehicleInfoTO, weatherInfoTO, trafficInfoTO);
			
			String predictedDelay = callPredictService(requestJson);
					
   
		   resultTO.setRoute(vehicleInfoTO.getRoute());
		   resultTO.setWeatherFactor(weatherInfoTO.printFactors());
		   resultTO.setWeatherIndication(weatherInfoTO.getEvent());
		   resultTO.setTrafficFactor(trafficInfoTO.printFactors());
		   resultTO.setTrafficIndication(trafficInfoTO.getTrafficCongestionIndex());
		   resultTO.setVehicleFactor(vehicleInfoTO.printFactors());
		   resultTO.setHolidayAdjIndication(vehicleInfoTO.getAdjHolidayInd());
		   
		   int delay = (int) Math.round(Double.parseDouble(predictedDelay));
		   
		   resultTO.setArrivalDelayMins(delay);
   
		   Date now = new Date();
		   String nowstr = now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
		   resultTO.setGeneratedTs(nowstr);
		   String eta = "-1";
					try {
						String scheduledArrivalTime = vehicleInfoTO.getScheduledArrivalTime();
						   SimpleDateFormat df = new SimpleDateFormat("HH:mm");
						   Date d = df.parse(scheduledArrivalTime); 
						   Calendar cal = Calendar.getInstance();
						   cal.setTime(d);
						   cal.add(Calendar.MINUTE, delay);
						   eta = df.format(cal.getTime());
					} catch (ParseException e) {
						resultTO.setAdditionalInfo("WARNING! Some Info has been modified for best resultss. Info:" + e.getMessage());
						e.printStackTrace(); 
					}
		   resultTO.setScheduledArrivalTime(vehicleInfoTO.getScheduledArrivalTime());
		   resultTO.setEta(eta);
		   
		   
		 //Check for vehicle breakdown scenario
			//Check against the latest data available in dump table
			//Get routeName, TripID
			//Find the entry for the given routeName-Trip-geoloc, count
		   VehicleLiveInfoTO vehicleLiveInfoTO = SmartServiceUtil.getVehicleLiveInfo(vehicleInfoTO.getRoute(), vehicleInfoTO.getTripID());
			if (! vehicleLiveInfoTO.isVehicleMoving() )
			{
				resultTO.setMajorWarningInd(true);
				resultTO.setMajorWarningMessage("Expected Major Delay as Vehicle movement has stopped for a while. We can assist you with alternate travel arrangements.");
				resultTO.setEta("Major Delay");
				resultTO.setArrivalDelayMins(999);
			}
			resultTO.setTimeLastKnown(vehicleLiveInfoTO.getTimeLastKnown());
			resultTO.setLatitudeLastKnown(Double.valueOf(vehicleLiveInfoTO.getLatitudeLastKnown()));
			resultTO.setLongitudeLastKnown(Double.valueOf(vehicleLiveInfoTO.getLongitudeLastKnown()));
			
			//get source and destination names
			String sourceStopKey = vehicleInfoTO.getRoute()+"-1";
			String destStopKey = vehicleInfoTO.getRoute()+"-"+vehicleInfoTO.getStopID();
			resultTO.setSource((String) SmartServiceUtil.getStopNameMap().get(sourceStopKey));
			resultTO.setDestination((String) SmartServiceUtil.getStopNameMap().get(destStopKey));
			
		} catch (Exception e) {
			e.printStackTrace();
			resultTO.setAdditionalInfo("WARNING! Feverish while calculating ETA. Info:" + e.getMessage());
		}
	   
      return resultTO; 
   }
	
	@GET 
	   @Path("/getVehicleLocation/{routeName}/{tripID}") 
		@Consumes(MediaType.APPLICATION_JSON) 
	   @Produces(MediaType.APPLICATION_JSON) 
		@ApiOperation(value="Get Vehicle Geo Location.", response=VehicleLiveInfoTO.class)
		@ApiResponses({ @ApiResponse(code = 200, response = VehicleLiveInfoTO.class, message = "Get Vehicle Geo Location") })
	   public VehicleLiveInfoTO getVehicleLocation(@PathParam("routeName") String routeName, @PathParam("tripID") String tripID){
			
		VehicleLiveInfoTO vehicleLiveInfoTO = SmartServiceUtil.getVehicleLiveInfo(routeName, tripID);
		
		return vehicleLiveInfoTO;
	}


	private TrafficInfoTO constructTrafficInfoTO(VehicleInfoTO vehicleInfoTO, Date travelDate) {
		TrafficInfoTO trafficInfoTO = new TrafficInfoTO();
		
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(travelDate);
			trafficInfoTO.setDate(String.valueOf(cal.get(Calendar.DATE)));
			trafficInfoTO.setMonth(String.valueOf(cal.get(Calendar.MONTH)+1));
			trafficInfoTO.setYear(String.valueOf(cal.get(Calendar.YEAR)));
			trafficInfoTO.setTime(vehicleInfoTO.getScheduledDepartureTime());
			
			
			Client client = ClientBuilder.newClient();

			//with minimum jam factor 6 for the entire route
			WebTarget resource = client.target("https://traffic.cit.api.here.com/traffic/6.1/flow.json?bbox=13.11,80.14;12.97,80.22&minjamfactor=6&app_id=tf3yQfmbpouRPrZKlgHR&app_code=GEbZOtQGURCbtgAxlJywFA");

			Builder request = resource.request().accept(MediaType.APPLICATION_JSON);

			Response response = request.get();
			if (response.getStatus() == 200) {
			    System.out.println("Success! " + response.getStatus());
			     System.out.println(response.getEntity());
			    
			} else {
			    System.out.println("ERROR! " + response.getStatus());
			    System.out.println(response.getEntity());
			}
			
			//System.out.println(response.readEntity(String.class));
//		jsonMap:{dt=1519399800, coord={lon=80.19, lat=13.07}, visibility=6000, weather=[{icon=02n, description=few clouds, main=Clouds, id=801}], name=Anna Nagar, cod=200, main={temp=298.15, temp_min=298.15, humidity=78, pressure=1012, temp_max=298.15}, clouds={all=20}, id=1465802, sys={country=IN, sunrise=1519347489, sunset=1519390024, id=7834, type=1, message=0.0068}, base=stations, wind={deg=50, speed=2.1}}
			Map jsonMap = response.readEntity(Map.class);
			
			String resultString = String.valueOf(jsonMap);
			if (resultString.indexOf("JF=9") != -1)
			{
				trafficInfoTO.setTrafficCongestionIndex("9.0");
			}
			else if (resultString.indexOf("JF=8") != -1)
			{
				trafficInfoTO.setTrafficCongestionIndex("8.0");
			}
			else if (resultString.indexOf("JF=7") != -1)
			{
				trafficInfoTO.setTrafficCongestionIndex("7.0");
			}
			else if (resultString.indexOf("JF=6") != -1)
			{
				trafficInfoTO.setTrafficCongestionIndex("6.0");
			}
			else
			{
				trafficInfoTO.setTrafficCongestionIndex("5.0");
			}
			//System.out.println("Traffic result string:" + resultString);
			
			System.out.println("constructed object - trafficInfoTO:" + trafficInfoTO);
		} catch (Exception e) {
			e.printStackTrace();
			trafficInfoTO.setAdditionalInfo("Warning! Some info are modified to bring best results. Info:" + e.getMessage());
			//set default values
			trafficInfoTO.setDate("1");
			trafficInfoTO.setMonth("1");
			trafficInfoTO.setYear("2018");
			trafficInfoTO.setTime("07:00");
			trafficInfoTO.setTrafficCongestionIndex("8.3");
		}
		//TEST BLOCK
		/*{
			trafficInfoTO.setDate("1");
			trafficInfoTO.setMonth("1");
			trafficInfoTO.setYear("2018");
			trafficInfoTO.setTime("07:00");
			trafficInfoTO.setTrafficCongestionIndex("8.3");
			
			System.out.println("Test object - trafficInfoTO:" + trafficInfoTO);
		}*/
		
		return trafficInfoTO;
	}


	private WeatherInfoTO constructWeatherInfoTO(Date travelDate) {
		//construct weather info
		WeatherInfoTO weatherInfoTO = new WeatherInfoTO();
		
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(travelDate);
			weatherInfoTO.setDate(String.valueOf(cal.get(Calendar.DATE)));
			weatherInfoTO.setMonth(String.valueOf(cal.get(Calendar.MONTH)+1));
			weatherInfoTO.setYear(String.valueOf(cal.get(Calendar.YEAR)));
			
			Client client = ClientBuilder.newClient();
			WebTarget resource = client.target("http://api.openweathermap.org/data/2.5/weather?APPID=ef58070dee8879a5c3f51c871e726d34&lat=13.069166&lon=80.191388&units=metric");

			Builder request = resource.request().accept(MediaType.APPLICATION_JSON);
			Response response = request.get();
			if (response.getStatus() == 200) {
			    System.out.println("Success! " + response.getStatus());
			     System.out.println(response.getEntity());
			    
			} else {
			    System.out.println("ERROR! " + response.getStatus());
			    System.out.println(response.getEntity());
			}
			
			//System.out.println(response.readEntity(String.class));
//		jsonMap:{dt=1519399800, coord={lon=80.19, lat=13.07}, visibility=6000, weather=[{icon=02n, description=few clouds, main=Clouds, id=801}], name=Anna Nagar, cod=200, main={temp=298.15, temp_min=298.15, humidity=78, pressure=1012, temp_max=298.15}, clouds={all=20}, id=1465802, sys={country=IN, sunrise=1519347489, sunset=1519390024, id=7834, type=1, message=0.0068}, base=stations, wind={deg=50, speed=2.1}}
			Map jsonMap = response.readEntity(Map.class);
			System.out.println("jsonMap:" + jsonMap);
			
			//System.out.println(((Map)((List)jsonMap.get("weather")).get(0)).get("main"));
			String event = String.valueOf(((Map)((List)jsonMap.get("weather")).get(0)).get("main"));
			String precip = String.valueOf(((Map)jsonMap.get("clouds")).get("all"));
			String temperature = String.valueOf(((Map)jsonMap.get("main")).get("temp"));
			String visibility = String.valueOf((((BigDecimal)jsonMap.get("visibility")).divide(new BigDecimal(1000)).toBigInteger().intValueExact()));
			String speed = String.valueOf( ((BigDecimal)((Map)jsonMap.get("wind")).get("speed")).toBigInteger().intValueExact());
			
			
			System.out.println("windspeed:" + speed);
			weatherInfoTO.setEvent(event);
			weatherInfoTO.setPrecipitation(precip);
			weatherInfoTO.setTempAvg(temperature);
			weatherInfoTO.setVisibility(visibility);
			weatherInfoTO.setWindSpeed(speed);
			
			System.out.println("constructed object - weatherInfoTO:" + weatherInfoTO);
		} catch (Exception e) {
			e.printStackTrace();
			weatherInfoTO.setAdditionalInfo("Warning! Some info are modified to bring best results. Info:" + e.getMessage());
			//Set default values
			weatherInfoTO.setDate("1");
			weatherInfoTO.setMonth("1");
			weatherInfoTO.setYear("2018");
			weatherInfoTO.setEvent("Thunderstorm");
			weatherInfoTO.setPrecipitation("100");
			weatherInfoTO.setTempAvg("20");
			weatherInfoTO.setVisibility("5");
			weatherInfoTO.setWindSpeed("20");
		}
		
		//TEST BLOCK
		/*{
				weatherInfoTO.setDate("1");
				weatherInfoTO.setMonth("1");
				weatherInfoTO.setYear("2018");
				weatherInfoTO.setEvent("Thunderstorm");
				weatherInfoTO.setPrecipitation("100");
				weatherInfoTO.setTempAvg("20");
				weatherInfoTO.setVisibility("5");
				weatherInfoTO.setWindSpeed("20");
				
				System.out.println("test object - weatherInfoTO:" + weatherInfoTO);
		}*/
		
		return weatherInfoTO;
	}


	private VehicleInfoTO constructVehicleInfoTO(String routeTripStopKey, Date travelDate) {
		//construct the vehicleInfoTO
		VehicleInfoTO vehicleInfoTO = new VehicleInfoTO();
		
		RouteInfoTO routeInfoTO = (RouteInfoTO) SmartServiceUtil.getRouteInfoMap().get(routeTripStopKey);
		System.out.println("routeTripStopKey:" + routeTripStopKey);
		System.out.println("to:" + routeInfoTO);
		if (routeInfoTO == null)
		{
			//set exception inf0
			vehicleInfoTO.setAdditionalInfo("Warning! Some info has been modified for best results. Info: Route not supported right now");
			//set default info
			routeInfoTO = new RouteInfoTO();
			routeInfoTO.setStopID("4");
			routeInfoTO.setRoute("D70");
			routeInfoTO.setTripID("1");
			routeInfoTO.setScheduledArrivalTime("07:00");
			routeInfoTO.setScheduledDepartureTime("07:00");
		}
		
		
		
		try {
			String adjHolidayInd = "No";
			List holidayList = SmartServiceUtil.getHolidayInfoList();
			
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			
			//Check if Next day holiday
			c.setTime(travelDate);
			c.add(Calendar.DATE, 1); 
			adjHolidayInd = (holidayList.contains(sdf.format(c.getTime())))?"Yes":"No";
			
			//Check if Previous day holiday
			if  (adjHolidayInd.equals("No"))
			{
				c.setTime(travelDate);
				c.add(Calendar.DATE, -1); 
				adjHolidayInd = (holidayList.contains(sdf.format(c.getTime())))?"Yes":"No";
			}
			vehicleInfoTO.setAdjHolidayInd(adjHolidayInd);
			vehicleInfoTO.setArrivalDelay("0"); //always 0?
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(travelDate);
			vehicleInfoTO.setDate(String.valueOf(cal.get(Calendar.DATE)));
			vehicleInfoTO.setMonth(String.valueOf(cal.get(Calendar.MONTH)+1));
			vehicleInfoTO.setYear(String.valueOf(cal.get(Calendar.YEAR)));
			DateFormat format2=new SimpleDateFormat("EEEE"); 
			vehicleInfoTO.setDayOfweek(format2.format(travelDate));

			vehicleInfoTO.setRoute(routeInfoTO.getRoute());
			vehicleInfoTO.setScheduledArrivalTime(routeInfoTO.getScheduledArrivalTime());
			vehicleInfoTO.setScheduledDepartureTime(routeInfoTO.getScheduledDepartureTime());
			vehicleInfoTO.setStopID(routeInfoTO.getStopID());
			vehicleInfoTO.setTripID(routeInfoTO.getTripID());
			
			System.out.println("constructed object - vehicleInfoTO:" + vehicleInfoTO);
		} catch (Exception e) {
			e.printStackTrace();
			//set exception info
			vehicleInfoTO.setAdditionalInfo("Warning! Some info are adjusted to bring best results. Info:" + e.getMessage());
			//Set default values
			vehicleInfoTO.setAdjHolidayInd("Yes");
			vehicleInfoTO.setArrivalDelay("0"); //always ?
			vehicleInfoTO.setDate("1");
			vehicleInfoTO.setMonth("1");
			vehicleInfoTO.setYear("2018");
			vehicleInfoTO.setDayOfweek("Sunday");
			vehicleInfoTO.setRoute("D70");
			vehicleInfoTO.setScheduledArrivalTime("07:00");
			vehicleInfoTO.setScheduledDepartureTime("07:00");
			vehicleInfoTO.setStopID("4");
			vehicleInfoTO.setTripID("1");			
		}
		
		//TEST BLOCK
		/*{
		vehicleInfoTO.setAdjHolidayInd("Yes");
		vehicleInfoTO.setArrivalDelay("0"); //always ?
		vehicleInfoTO.setDate("1");
		vehicleInfoTO.setMonth("1");
		vehicleInfoTO.setYear("2018");
		vehicleInfoTO.setDayOfweek("Sunday");
		vehicleInfoTO.setRoute("D70");
		vehicleInfoTO.setScheduledArrivalTime("07:00");
		vehicleInfoTO.setScheduledDepartureTime("07:00");
		vehicleInfoTO.setStopID("4");
		vehicleInfoTO.setTripID("1");
		
		System.out.println("test object - vehicleInfoTO:" + vehicleInfoTO);
		}*/
		
		return vehicleInfoTO;
	}

	
	
	private String constructPredictServiceRequest(VehicleInfoTO vehicleInfoTO, WeatherInfoTO weatherInfoTO, TrafficInfoTO trafficInfoTO) {
		
		
		JSONObject json = new JSONObject();
		
		JSONObject inputsJson = new JSONObject();
		
		JSONObject vehicleInputJson = new JSONObject();
		vehicleInputJson.put("ColumnNames", Arrays.asList("Route","Stop","Trip","Sch Dep","Sch Arrival","Month","Date","Year","Day of the Week","Adj Holiday","Arrival Delay"));
		
		JSONArray vehicleInputValuesArray = new JSONArray();
		//vehicleInputValuesArray.put(Arrays.asList("D70","4","2","07:00","07:00","1","1","2018","Sunday","No","0"));
		vehicleInputValuesArray.put(Arrays.asList(vehicleInfoTO.getRoute(),vehicleInfoTO.getStopID(),vehicleInfoTO.getTripID(),
				vehicleInfoTO.getScheduledDepartureTime(),vehicleInfoTO.getScheduledArrivalTime(),
				vehicleInfoTO.getMonth(),vehicleInfoTO.getDate(),vehicleInfoTO.getYear(),vehicleInfoTO.getDayOfweek(),
				vehicleInfoTO.getAdjHolidayInd(),vehicleInfoTO.getArrivalDelay()));
		vehicleInputJson.put("Values", vehicleInputValuesArray);
		
		inputsJson.put("vehicleInput", vehicleInputJson);
		
		JSONObject weatherInputJson = new JSONObject();
		weatherInputJson.put("ColumnNames", Arrays.asList("Year","Month","Date","temp avg","visibility avg km","wind high","precip mm","Events"));
		
		JSONArray weatherInputValuesArray = new JSONArray();
		//weatherInputValuesArray.put(Arrays.asList("2018","1","1","20","3","2","100","Thunderstorm"));
		weatherInputValuesArray.put(Arrays.asList(weatherInfoTO.getYear(),weatherInfoTO.getMonth(),weatherInfoTO.getDate(),
				weatherInfoTO.getTempAvg(),weatherInfoTO.getVisibility(),weatherInfoTO.getWindSpeed(),
				weatherInfoTO.getPrecipitation(),weatherInfoTO.getEvent()));
		weatherInputJson.put("Values", weatherInputValuesArray);
		
		inputsJson.put("weatherInput", weatherInputJson);
		
		JSONObject trafficInputJson = new JSONObject();
		trafficInputJson.put("ColumnNames", Arrays.asList("Time","Month","Date","Year","Traffic Congestion Index"));
		
		JSONArray trafficInputValuesArray = new JSONArray();
		//trafficInputValuesArray.put(Arrays.asList("07:00","1","1","2018","8.2"));
		trafficInputValuesArray.put(Arrays.asList(trafficInfoTO.getTime(),trafficInfoTO.getMonth(),trafficInfoTO.getDate(),trafficInfoTO.getYear(),trafficInfoTO.getTrafficCongestionIndex()));
		trafficInputJson.put("Values", trafficInputValuesArray);
		
		inputsJson.put("trafficInput", trafficInputJson);
		
		json.put("Inputs", inputsJson);
		System.out.println("json:" + json);
		
		return json.toString();
	}

	private String callPredictService(String requestJsonString) {
		String predictedDelay = "-1";
		try {
			
			HttpPost post = new HttpPost("https://ussouthcentral.services.azureml.net/workspaces/685f1a13df4d4c9f90ff623566b7898d/services/00a869b06432429a804486fab2c883f1/execute?api-version=2.0&details=true");
			HttpClient client = HttpClientBuilder.create().build();
			
			StringEntity entity = new StringEntity(requestJsonString,HTTP.UTF_8);
			
			entity.setContentType("text/json");
			post.setHeader("Accept", "text/json");
			post.setHeader("Authorization", ("Bearer "+ "YFo+Lp9zEwLkJOy4hffoAa26RAbXnUyGsPd1X3FM816mCK/tdG5JvSAt5XP72c93t5wsW1Ykcu63REgv8PgLlQ=="));
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();
			String result = EntityUtils.toString(httpEntity);
			
			System.out.println("result:" + result);

			JSONObject jsonObj = new JSONObject (result); 
			System.out.println("json:"+ jsonObj);
			
			if(jsonObj != null)
			{
				JSONArray valueArr = jsonObj.getJSONObject("Results").getJSONObject("ArrivalDelayResult").getJSONObject("value").getJSONArray("Values");
				System.out.println("array:" + valueArr);
				JSONArray list = (JSONArray) valueArr.get(0);
				System.out.println("list:" + list);
				predictedDelay = (String) list.get(1);
			}
			System.out.println("predictedDelay:" + predictedDelay);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return predictedDelay;
	}
	
   @GET 
   @Path("/hello") 
   @Produces(MediaType.APPLICATION_JSON) 
   @ApiOperation(value="Say Hello - Test API", response=ResultTO.class)
   @ApiResponses({ @ApiResponse(code = 200, response = ResultTO.class, message = "Get a constant response while saying hello") })
   public ResultTO sayHello(){ 
	   
	   ResultTO resultTO = new ResultTO();
	   resultTO.setRoute("R123");
	   resultTO.setArrivalDelayMins(30);
      return resultTO; 
   }  
}