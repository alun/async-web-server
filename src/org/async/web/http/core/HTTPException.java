package org.async.web.http.core;

public class HTTPException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private int errCode;

	public HTTPException(int errCode) {
		super(String.valueOf(errCode));
		this.errCode = errCode;
	}

	public HTTPException(int errCode,String message) {
		super(String.valueOf(errCode)+" "+message);
		this.errCode = errCode;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

}
