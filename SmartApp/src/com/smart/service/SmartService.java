package com.smart.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import com.smart.service.util.SmartServiceUtil;
import com.smart.to.Input1;
import com.smart.to.Input2;
import com.smart.to.Inputs;
import com.smart.to.PredictSvcRequest;
import com.smart.to.RequestTO;
import com.smart.to.ResultTO;
import com.smart.util.to.VehicleRouteEntity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value="/")

@Path("/smartService") 
public class SmartService {  
	
	@POST 
   @Path("/getEta") 
	@Consumes(MediaType.APPLICATION_JSON) 
   @Produces(MediaType.APPLICATION_JSON) 
	
			 
	@ApiOperation(value="Get ETA.", response=ResultTO.class)
	@ApiResponses({ @ApiResponse(code = 200, response = ResultTO.class, message = "Get ETA time") })
   public ResultTO getEta(RequestTO requestTO){
		
		//get route from request
		String routeKey = requestTO.getRouteKey();
		
		//gather vehicle info with route 
		VehicleRouteEntity entity = SmartServiceUtil.getRouteInfo(routeKey);
		
		//get traffic info
		
		callTrafficService();
		
		//get weather info
		
		callWeatherService();
		
		
		//call arrival delay prediction service
		
		PredictSvcRequest predictSvcRequest = constructPredictSvcRequest(entity.getRoute());
		
		
		String predictedDelay = "0";
		
		predictedDelay = callPredictService(predictSvcRequest);
				
	   ResultTO resultTO = new ResultTO();
	   resultTO.setRoute(entity.getRoute());
	   int delay = (int) Math.round(Double.parseDouble(predictedDelay));
	   
	   resultTO.setArrivalDelay("" + delay + " Mins");
	   Date now = new Date();
	   String eta = now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
	   resultTO.setEta("Today " + eta + " " + entity.getScheduledArrivalTime());
	   
      return resultTO; 
   }


	private String callPredictService(PredictSvcRequest predictSvcRequest) {
		String predictedDelay = "0";
		try {
			ObjectWriter ow = new ObjectMapper().writer();
			String json = ow.writeValueAsString(predictSvcRequest);
			
			System.out.println("PredictiveServiceRequest-json:" + json);
			
			
			//String jsonRequest = "{  \"Inputs\": { \"input2\": {\"ColumnNames\": [\"Month\",\"Date\",\"Day of week\",\"Time\",\"Temp\",\"Weather\",\"Wind\",\"Humidity\",\"Barometer\",\"Visibility\"], \"Values\": [[\"1\",\"1\",\"2\",\"2018-02-10T00:00:00Z\",\"23 °C\",\"Haze.\",\"No wind\",\"83%\",\"1012 mbar\",\"5 km\"]] }, \"input1\": {\"ColumnNames\": [\"Year\",\"Month\",\"DayofMonth\",\"DayOfWeek\",\"Route No\",\"Trip No\",\"OriginHopID\",\"DestHopID\",\"OriginDepTime\",\"DepDelay\",\"DestArrTime\",\"ArrDelay\"], \"Values\": [[\"2017\",\"1\",\"1\",\"2\",\"R1\",\"1\",\"H12\",\"H23\",\"08:00:00\",\"-3\",\"11:00:00\",\"1\"]] }  }, \"GlobalParameters\": {}}";
			
			HttpPost post = new HttpPost("https://ussouthcentral.services.azureml.net/workspaces/685f1a13df4d4c9f90ff623566b7898d/services/8928b8cd46df4310aec1add087539b9e/execute?api-version=2.0&details=true");
			HttpClient client = HttpClientBuilder.create().build();
			
			
			StringEntity entity = new StringEntity(json,HTTP.UTF_8);
			
			entity.setContentType("text/json");
			
			post.setHeader("Accept", "text/json");
			
			
			post.setHeader("Authorization", ("Bearer "+ "s0wByZGfbj6dOy+Q100WfsE077Rq1T3lFdKclRatJIB+8rmlF14CPSL90k1HOmDqjAAn7eDmruSYATuyIwc93A=="));
			post.setEntity(entity);

			
			HttpResponse response = client.execute(post);
			
			HttpEntity httpEntity = response.getEntity();
			
			String result = EntityUtils.toString(httpEntity);
			
			System.out.println("result:" + result);
			
			
			JSONObject jsonObj = new JSONObject (result); 
			System.out.println("json:"+ jsonObj);
			
			if(jsonObj != null)
			{
				JSONArray valueArr = jsonObj.getJSONObject("Results").getJSONObject("output1").getJSONObject("value").getJSONArray("Values");
				System.out.println("array:" + valueArr);
				JSONArray list = (JSONArray) valueArr.get(0);
				System.out.println("list:" + list);
				predictedDelay = (String) list.get(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return predictedDelay;
	}

	//service test with default inputs
	private PredictSvcRequest constructPredictSvcRequest() {
		PredictSvcRequest predictSvcRequest = new PredictSvcRequest();
		Inputs inputs = new Inputs();
		Input1 input1 = new Input1();
		List<String> input1ColumnNames = new ArrayList<String> (Arrays.asList("Year","Month","DayofMonth","DayOfWeek","Route No","Trip No","OriginHopID","DestHopID","OriginDepTime","DepDelay","DestArrTime","ArrDelay")) ;
		input1.setColumnNames(input1ColumnNames);
		List<String> input1Values = new ArrayList<String> (Arrays.asList("2017","1","1","2","R1","1","H12","H23","08:00:00","-3","11:00:00","1"));
		input1.setValues(new ArrayList(Arrays.asList(input1Values)));
		inputs.setInput1(input1);
		
		Input2 input2 = new Input2();
		List<String> input2ColumnNames = new ArrayList<String> (Arrays.asList("Month","Date","Day of week","Time","Temp","Weather","Wind","Humidity","Barometer","Visibility")) ;
		input2.setColumnNames(input2ColumnNames);
		List<String> input2Values = new ArrayList<String> (Arrays.asList("1","1","2","2018-02-10T00:00:00Z","23 °C","Haze.","No wind","83%","1012 mbar","5 km"));
		input2.setValues(new ArrayList(Arrays.asList(input2Values)));
		input2.setColumnNames(input2ColumnNames);
		inputs.setInput2(input2);
		predictSvcRequest.setInputs(inputs);
		return predictSvcRequest;
	}
	
	//service test with default inputs
	private PredictSvcRequest constructPredictSvcRequest(String route) {
		
		
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String month = String.valueOf(calendar.get(Calendar.MONTH));
		String dayOfMonth = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		String dayOfWeek = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
		
		System.out.println("inputs:" + "year:" + year + "month:" + month + "dayOfMonth:" + dayOfMonth + "dayOfWeek:" + dayOfWeek);
		
		SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = localDateFormat.format(new Date());
		String originDepTime = String.valueOf(time);
		
		PredictSvcRequest predictSvcRequest = new PredictSvcRequest();
		Inputs inputs = new Inputs();
		Input1 input1 = new Input1();
		List<String> input1ColumnNames = new ArrayList<String> (Arrays.asList("Year","Month","DayofMonth","DayOfWeek","Route No","Trip No","OriginHopID","DestHopID","OriginDepTime","DepDelay","DestArrTime","ArrDelay")) ;
		input1.setColumnNames(input1ColumnNames);
		List<String> input1Values = new ArrayList<String> (Arrays.asList(year,month,"1",dayOfWeek,route,"1","H12","H23","08:00:00","-3","11:00:00","1"));
		input1.setValues(new ArrayList(Arrays.asList(input1Values)));
		inputs.setInput1(input1);
		
		Input2 input2 = new Input2();
		List<String> input2ColumnNames = new ArrayList<String> (Arrays.asList("Month","Date","Day of week","Time","Temp","Weather","Wind","Humidity","Barometer","Visibility")) ;
		input2.setColumnNames(input2ColumnNames);
		List<String> input2Values = new ArrayList<String> (Arrays.asList("1","1","2","2018-02-10T00:00:00Z","23 °C","Haze.","No wind","83%","1012 mbar","5 km"));
		input2.setValues(new ArrayList(Arrays.asList(input2Values)));
		input2.setColumnNames(input2ColumnNames);
		inputs.setInput2(input2);
		predictSvcRequest.setInputs(inputs);
		return predictSvcRequest;
	}
	
	private void callWeatherService()
	{
		//Koyambed lat-long 13.069166,80.191388
		//Guindy lat-long 13.010236, 80.215651
		//Velachery lat-long 12.975971, 80.221209
		
				
		Client client = ClientBuilder.newClient();

		WebTarget resource = client.target("http://api.openweathermap.org/data/2.5/weather?APPID=ef58070dee8879a5c3f51c871e726d34&lat=13.069166&lon=80.191388");

		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);

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
		
		BigDecimal speed = (BigDecimal) ((Map)jsonMap.get("wind")).get("speed");
		
		System.out.println("windspeed:" + speed);
		
		//System.out.println("response:" + response);
	}
	
	private void callTrafficService()
	{
		//Koyambed lat-long 13.069166,80.191388
		//Guindy lat-long 13.010236, 80.215651
		//Velachery lat-long 12.975971, 80.221209
				
		Client client = ClientBuilder.newClient();

		WebTarget resource = client.target("https://traffic.cit.api.here.com/traffic/6.1/flow.json?minjamfactor=7&corridor=13.0691%2C80.1913%3B13.0102%2C80.2156%3B12.9759%2C80.2212%3B1000&app_id=tf3yQfmbpouRPrZKlgHR&app_code=GEbZOtQGURCbtgAxlJywFA");

		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);

		Response response = request.get();
		if (response.getStatus() == 200) {
		    System.out.println("Success! " + response.getStatus());
		     System.out.println(response.getEntity());
		    
		} else {
		    System.out.println("ERROR! " + response.getStatus());
		    System.out.println(response.getEntity());
		}
		
		System.out.println(response.readEntity(String.class));
		
		/*Map jsonMap = response.readEntity(Map.class);
		System.out.println("jsonMap:" + jsonMap);
		
		BigDecimal speed = (BigDecimal) ((Map)jsonMap.get("wind")).get("speed");
		
		System.out.println("windspeed:" + speed);
	*/	
		//System.out.println("response:" + response);
	}
	
   @GET 
   @Path("/hello") 
   @Produces(MediaType.APPLICATION_JSON) 
   @ApiOperation(value="Say Hello - Test API", response=ResultTO.class)
   @ApiResponses({ @ApiResponse(code = 200, response = ResultTO.class, message = "Get a constant response while saying hello") })
   public ResultTO sayHello(){ 
	   
	   ResultTO resultTO = new ResultTO();
	   resultTO.setRoute("R123");
	   resultTO.setArrivalDelay("30 Mins");
      return resultTO; 
   }  
}