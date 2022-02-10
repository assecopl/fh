package pl.fhframework.dp.commons.services.mail;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AttachmentDataSource implements DataSource {

	private byte[] content;
	private String name;
	private String contentType;
	
	
	
	

	public AttachmentDataSource(byte[] content, String name, String contentType) {
		super();
		this.content = content;
		this.name = name;
		this.contentType = contentType;
	}
	
	
	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return contentType;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(content);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
