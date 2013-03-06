package graphic;


public class PColor {
		 
		public static final PColor black 	= new PColor(0,     0,   0);
		public final static PColor red      = new PColor(255,   0,   0);
		public final static PColor yellow 	= new PColor(255, 255,   0);
		public final static PColor orange 	= new PColor(255, 200,   0);
		public final static PColor pink     = new PColor(255, 175, 175);
		public final static PColor green 	= new PColor(0,   255,   0);
		public final static PColor blue 	= new PColor(0,     0, 255);
		public final static PColor gray     = new PColor(128, 128, 128);
		public final static PColor white    = new PColor(255, 255, 255);
		
		private int value;
		
		public PColor(int r, int g, int b, int a) {
	        boolean rangeError = false;
	        if ( r < 0 || r > 255)
	        	rangeError = true;
	    	if ( g < 0 || g > 255)
	    	    rangeError = true;
	    	if ( b < 0 || b > 255)
	    	    rangeError = true;
	    	if (rangeError)
	    		throw new IllegalArgumentException();
	    	
	    	value = ((a & 0xFF) << 24) |
	                ((r & 0xFF) << 16) |
	                ((g & 0xFF) << 8)  |
	                ((b & 0xFF) << 0);
		}
		
		public PColor(int r, int g, int b) {
	        this(r, g, b, 255);
		}
		public int getRGB() {
			return value;
		}
		@Override
		public int hashCode() {
			return value;
		}
		@Override
		public boolean equals(Object o){
			return o instanceof PColor && ((PColor) o).getRGB() == this.getRGB();
		}
		
	}
