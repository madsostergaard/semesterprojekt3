package serverside;

import java.util.ArrayList;

public class Notices {

	private String uuid;
	private String status;
	private ArrayList<Notice> notice;

	Notices(String uuid) {

		this.uuid = uuid;
	}

	Notices(String uuid, ArrayList<Notice> notice) {

		this.uuid = uuid;
		this.notice = notice;

	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ArrayList<Notice> getNotice() {
		return notice;
	}

	public void setNotice(ArrayList<Notice> notice) {
		this.notice = notice;
	}

	public void addNotice(Notice notice) {
		if (this.notice == null)
			this.notice = new ArrayList<>();
		this.notice.add(notice);

	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
