package serverside;

import java.util.ArrayList;

public class Notices {

	private int uuid;
	private ArrayList<Notice> notice;

	Notices(int uuid) {

		this.uuid = uuid;
	}

	Notices(int uuid, ArrayList<Notice> notice) {

		this.uuid = uuid;
		this.notice = notice;

	}

	public int getUuid() {
		return uuid;
	}

	public void setUuid(int uuid) {
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
