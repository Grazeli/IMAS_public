package format;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Answer implements Serializable {
	
	ArrayList<Double> outputs = new ArrayList<Double>();
	String filepath;
	
	public Answer(ArrayList<Double> outputs2, String filepath) {
		super();
		this.outputs = outputs2;
		this.filepath = filepath;
	}
	public ArrayList<Double> getOutputs() {
		return outputs;
	}
	public void setOutputs(ArrayList<Double> outputs) {
		this.outputs = outputs;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
	
}
