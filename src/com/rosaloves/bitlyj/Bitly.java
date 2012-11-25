package com.rosaloves.bitlyj;

public final class Bitly {
	public static Provider as(String user, String apiKey) {
		return new SimpleProvider("http://bit.ly/", user, apiKey,
				"http://api.bit.ly/v3/");
	}

	public static MethodBase<Object> info(String value) {
		return Methods.info(value);
	}

	public static MethodBase<Object> info(String[] value) {
		return Methods.info(value);
	}

	public static MethodBase<Object> expand(String value) {
		return Methods.expand(value);
	}

	public static MethodBase<Object> expand(String[] value) {
		return Methods.expand(value);
	}

	public static MethodBase<Object> shorten(String longUrl) {
		return Methods.shorten(longUrl);
	}

	public static MethodBase<Object> clicks(String string) {
		return Methods.clicks(string);
	}

	public static MethodBase<Object> clicks(String[] string) {
		return Methods.clicks(string);
	}

	public static abstract interface Provider {
		public abstract <A> A call(BitlyMethod<A> paramBitlyMethod);

		public abstract String getUrl();
	}
}