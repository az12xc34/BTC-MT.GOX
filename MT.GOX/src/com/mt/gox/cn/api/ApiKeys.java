package com.mt.gox.cn.api;

/**
 * @ClassName: ApiKeys
 * @Description:
 * @author LEIKANG
 */
public class ApiKeys {

	private String privateKey, apiKey;

	public ApiKeys(String privateKey, String apiKey) {
		this.privateKey = privateKey;
		this.apiKey = apiKey;
	}

	/**
	 * @return the privateKey
	 */
	public String getPrivateKey() {
		return privateKey;
	}

	/**
	 * @param privateKey
	 *            the privateKey to set
	 */
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * @param apiKey
	 *            the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
}
