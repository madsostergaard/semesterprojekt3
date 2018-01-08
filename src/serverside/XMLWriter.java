package serverside;
public class XMLWriter {

	private String out = "";
	private int indent = 0;
	private StringBuilder sb = null;

	public XMLWriter() {
		sb = new StringBuilder();

		writeHead();
	}

	private void writeHead() {
		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
	}

	public void writeStart(String tag) {
		writeStart(tag, false); 
	}

	private void writeStart(String tag, boolean contained) {
		// for (int i = 0; i < indent; i++)
		// sb.append("\t");
		if (contained)
			sb.append("<" + tag + ">");
		else
			sb.append("<" + tag + ">");
		indent++;
	}

	public void writeEnd(String tag) {
		writeEnd(tag, false);
	}

	private void writeEnd(String tag, boolean contained) {
		indent--;
		// if (!contained)
		// for (int i = 0; i < indent; i++) sb.append("\t");
		sb.append("</" + tag + ">");
	}

	public void writeTag(String tag, String content, boolean contained) {
		writeStart(tag, contained);
		if (contained)
			sb.append(content);
		else {
			// for (int i = 0; i < indent; i++) sb.append("\t");
			sb.append(content);
		}
		writeEnd(tag, contained);
	}

	public String getXML() {
		return sb.toString();
	}

	public void reset() {
		sb = new StringBuilder();
		writeHead();
	}
}
