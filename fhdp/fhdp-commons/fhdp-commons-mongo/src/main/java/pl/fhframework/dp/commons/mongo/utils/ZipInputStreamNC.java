package pl.fhframework.dp.commons.mongo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class ZipInputStreamNC extends ZipInputStream {

	public ZipInputStreamNC(InputStream in) {
		super(in);
	}
	
	@Override
	public void close() throws IOException {
		//closing stream without permission is big NO NO
	}

	public void trueClose() throws IOException {
		super.close();
	}

}
