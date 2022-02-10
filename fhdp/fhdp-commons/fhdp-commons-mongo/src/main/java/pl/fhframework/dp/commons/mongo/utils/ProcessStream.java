package pl.fhframework.dp.commons.mongo.utils;

import java.io.*;

public abstract class ProcessStream extends Thread {
	
	PipedOutputStream outputStream = new PipedOutputStream();
	PipedInputStream pis;
	OutputStream target;
	
	public ProcessStream(OutputStream target) {
		super();
		this.target = target;
		try {
			pis = new PipedInputStream(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		
		try {
			processStream(pis, target);
			target.flush();
			target.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}




	public OutputStream getOutputStream() {
		return outputStream;
	}

	
	public abstract void processStream(InputStream is, OutputStream os) throws IOException;

}
