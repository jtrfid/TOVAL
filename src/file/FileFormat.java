package file;

import java.nio.charset.Charset;


public abstract class FileFormat {
	
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	protected Charset charset = DEFAULT_CHARSET;

	public FileFormat() {}
	
	public FileFormat(Charset charset)  throws IllegalArgumentException{
		setCharset(charset);
	}
	
	public Charset getCharset(){
		return this.charset;
	}
	
	public void setCharset(Charset charset) throws IllegalArgumentException{
		if(!supportsCharset(charset))
			throw new IllegalArgumentException("Charset not supported");
		this.charset = charset;
	}
	
	public abstract String getName();

	public abstract boolean supportsCharset(Charset charset);
	
	public abstract String getFileExtension();
	
	public abstract String getFileHeader();
	
	public abstract String getFileFooter();

}
