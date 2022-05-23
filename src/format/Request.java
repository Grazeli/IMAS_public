package format;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Request implements Serializable {
	
	private String application;
	List<ArrayList<Double>> requests = new ArrayList<ArrayList<Double>>();
	//ArrayList<Float> list1 = new ArrayList<Float>();
	//list1.add(param1);
	//list1.add(param2);
	//listOfLists.add(list);
	public Request(String application, List<ArrayList<Double>> requests) {
		super();
		this.application = application;
		this.requests = requests;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public List<ArrayList<Double>> getRequests() {
		return requests;
	}
	public void setRequests(List<ArrayList<Double>> requests) {
		this.requests = requests;
	}
	
}
