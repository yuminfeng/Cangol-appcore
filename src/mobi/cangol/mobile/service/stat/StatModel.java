package mobi.cangol.mobile.service.stat;

import java.util.Map;

final public class StatModel {
	private String url;
	private Map<String, String> params;

	public StatModel(String url, Map<String, String> params) {
		this.url = url;
		this.params = params;
	}

	public String getUrl() {
		return url;
	}

	public Map<String, String> getParams() {
		return params;
	}

}