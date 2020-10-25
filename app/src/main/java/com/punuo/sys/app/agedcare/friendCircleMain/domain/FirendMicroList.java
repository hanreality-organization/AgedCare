package com.punuo.sys.app.agedcare.friendCircleMain.domain;

import java.util.List;

public class FirendMicroList extends MyBaseBean {
    private String total;
    private String per_page;
    private String current_page;
    private String last_page;
    private List<FirendMicroListDatas> data;

    public List<FirendMicroListDatas> getDatas() {
        return data;
    }

    public String getCurrent_page() {
        return current_page;
    }

    public String getLast_page() {
        return last_page;
    }

    public String getPer_page() {
        return per_page;
    }

    public String getTotal() {
        return total;
    }

    public void setCurrent_page(String current_page) {
        this.current_page = current_page;
    }

    public void setData(List<FirendMicroListDatas> data) {
        this.data = data;
    }

    public void setLast_page(String last_page) {
        this.last_page = last_page;
    }

    public void setPer_page(String per_page) {
        this.per_page = per_page;
    }

    public void setTotal(String total) {
        this.total = total;
    }
    //	private String offset;
//	private String showNum;
//	private String total;
//	private List<FirendMicroListDatas> datas;
//
//	public String getOffset() {
//		return offset;
//	}
//	public void setOffset(String offset) {
//		this.offset = offset;
//	}
//	public String getShowNum() {
//		return showNum;
//	}
//	public void setShowNum(String showNum) {
//		this.showNum = showNum;
//	}
//	public String getTotal() {
//		return total;
//	}
//	public void setTotal(String total) {
//		this.total = total;
//	}
//	public List<FirendMicroListDatas> getDatas() {
//		return datas;
//	}
//	public void setDatas(List<FirendMicroListDatas> datas) {
//		this.datas = datas;
//	}
}
