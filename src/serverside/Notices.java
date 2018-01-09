package serverside;

import java.util.ArrayList;

public class Notices {

	private String uuid;
	private ArrayList<Notice> notice;
	private String status;

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

		this.notice.add(notice);

	}

}
