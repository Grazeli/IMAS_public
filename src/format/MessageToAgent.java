package format;
import java.io.Serializable;
import java.util.ArrayList;

public class MessageToAgent implements Serializable {
	
	private String type;
	Object object;
	
	public MessageToAgent(String type, Object object) {
		super();
		this.type = type;
		this.object = object;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
	
}
