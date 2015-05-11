package de.invation.code.toval.graphic.diagrams.panels;
import de.invation.code.toval.math.MathUtils;
import de.invation.code.toval.misc.FormatUtils;

	/**
	 * Class for managing information about ticks adjusted to i.e. axes or lines.<br>
	 * Ticks  have major and minor ticks which can have different sizes (lengths).
	 * The specified tickRange applies to minor ticks; major tick ranges are
	 * defined as multiples of minor ticks with the help of an adjustable multiplicator.
	 * Basically there are two operation modes: zero-based or not.<br>
	 * In zero-based mode ticks always start at 0 and end near the maximum value of the corresponding value range.
	 * Otherwise ticks start at the minimum value.
	 */
	public class TickInfo {
		
		/**
		 * Major tick length.
		 */
		private int majorTickLength = 8;
		/**
		 * Minor tick length.
		 */
		private int minorTickLength = 4;
		/**
		 * Minor tick spacing.
		 */
		private double minorTickSpacing = 1.0;
		/**
		 * Multiplicator for calculating the major tick spacing out of the minor tick spacing.
		 */
		private int tickMultiplicator = 10;
		/**
		 * Format for String representations of tick values.<br>
		 * If tick values are non-integer values they are formatted as floating point values with
		 * the same precision as the minor tick spacing.
		 */
		private String format;
		/**
		 * Minimum value of the corresponding value range.
		 */
		private double minValue;
		/**
		 * Maximum value of the corresponding value range.
		 */
		private double maxValue;
		/**
		 * Value range between <code>minValue</code> and <code>maxValue</code>.
		 */
		private double range;
		/**
		 * Value of the first tick.<br>
		 * In zero based mode the first tick is 0, otherwise it equals {@link #minValue}
		 */
		private double firstTick;
		/**
		 * Value of the last tick.<br>
		 * This value is determined on the basis of the first ticks' value and the overall number of ticks.
		 */
		private double lastTick;
		/**
		 * Defines the value range between the first and last tick.
		 */
		private double tickRange;
		/**
		 * The overall number of ticks.
		 */
		private int tickNumber;
		/**
		 * The basic operation mode for tick management.<br>
		 * Zero based means that ticks always start at 0 and end near the maximum value of the corresponding value range.
	     * Otherwise ticks start at the minimum value.
		 */
		private boolean zeroBased;
		
		/**
		 * Creates an new tick management object with the specified operation mode,
		 * using the given minimum and maximum values as bounds of a corresponding value range.
		 * @param minValue Minimum value of the corresponding value range
		 * @param maxValue Maximum value of the corresponding value range
		 * @param zeroBased Operation mode
		 * @see #zeroBased
		 */
		public TickInfo(double minValue, double maxValue, boolean zeroBased) {
			this.minValue = minValue;
			this.maxValue = maxValue;
			range = maxValue-Math.signum(minValue)*Math.abs(minValue);
			minorTickSpacing = range/10.0;
			setZeroBased(zeroBased);
			setFormat();
		}
		
		/**
		 * Sets the operation mode and adjusts tick values accordingly.
		 * In zero-based mode ticks always start at 0 and end near the maximum value of the corresponding value range.
		 * Otherwise ticks start at the minimum value.
		 * @param zeroBased Operation mode
		 * @see #setTicks()
		 */
		public void setZeroBased(boolean zeroBased) {
			this.zeroBased = zeroBased;
			setTicks();
		}
		
		/**
		 * Sets tick values and tick range according to the current operation mode.<br>
		 * In zero based mode the first tick is 0 and the last tick is determined on the basis of the first tick and the number of overall ticks.
		 * Otherwise the first tick equals {@link #minValue}.
		 */
		private void setTicks() {
			double range, z;
			if(zeroBased) {
				firstTick = 0.0;
			} else {
				range = Math.abs(minValue);
				z = (range % getMinorTickSpacing())==0 ? 0 : 1; 
				firstTick = Math.signum(minValue)*getMinorTickSpacing()*(Math.floor(range/getMinorTickSpacing())+z); 	
			}
			range = Math.abs(maxValue);
			z = (range % getMinorTickSpacing())==0 ? 0 : 1; 
			lastTick = Math.signum(maxValue)*getMinorTickSpacing()*(Math.floor(range/getMinorTickSpacing())+z); 
			tickNumber = (int) ((lastTick-Math.signum(firstTick)*Math.abs(firstTick))/getMinorTickSpacing())+1;
			tickRange = lastTick-Math.signum(firstTick)*Math.abs(firstTick);
//			System.out.println("first tick: "+firstTick);
//			System.out.println(" last tick: "+lastTick);
//			System.out.println("tick number: "+tickNumber);
//			System.out.println("tick range: "+tickRange);
		}
		
		/**
		 * Returns the value of the first tick.
		 * @return Value of the first tick
		 */
		public double getFirstTick() {
			return firstTick;
		}
		
		/**
		 * Returns the value of the last tick.
		 * @return Value of the last tick
		 */
		public double getLastTick() {
			return lastTick;
		}
		
		/**
		 * Returns the value range between the first and last tick.
		 * @return Value range between the first and last tick
		 */
		public double getTickRange() {
			return tickRange;
		}

		/**
		 * Returns the total number of ticks.
		 * @return Number of ticks
		 */
		public int getTickNumber() {
			return tickNumber;
		}

		/**
		 * Returns the major tick length.
		 * @return Major tic klength
		 */
		public int getMajorTickLength() {
			return majorTickLength;
		}

		/**
		 * Sets the major tick length
		 * @param majorTickLength Length of major ticks
		 */
		public void setMajorTickLength(int majorTickLength) {
			this.majorTickLength = majorTickLength;
		}

		/**
		 * Returns th e minor tick length
		 * @return Minor tick length
		 */
		public int getMinorTickLength() {
			return minorTickLength;
		}

		/**
		 * Sets the minor tick length
		 * @param minorTickLength Length of minor ticks
		 */
		public void setMinorTickLength(int minorTickLength) {
			this.minorTickLength = minorTickLength;
		}

		/**
		 * Returns the minor tick spacing
		 * @return Minor tick spacing
		 */
		public double getMinorTickSpacing() {
			return minorTickSpacing;
		}
		
		/**
		 * Sets the tick spacing.<br>
		 * <code>minorTickSpacing</code> sets the minor tick spacing,
		 * major tick spacing is a multiple of minor tick spacing and determined with the help of a multiplicator.
		 * @param minorTickSpacing Minor tick spacing
		 * @see #tickMultiplicator
		 */
		public void setTickSpacing(double minorTickSpacing) {
			setTickSpacing(minorTickSpacing, getTickMultiplicator());
		}

		/**
		 * Sets the tick spacing.<br>
		 * <code>minorTickSpacing</code> sets the minor tick spacing,
		 * major tick spacing is a multiple of minor tick spacing and determined with the help of <code>multiplicator</code> 
		 * @param minorTickSpacing Minor tick spacing
		 * @param multiplicator Multiplicator for detrermining the major tick spacing.
		 */
		public void setTickSpacing(double minorTickSpacing, int multiplicator) {
			if(Double.isInfinite(minorTickSpacing) || Double.isNaN(minorTickSpacing))
				throw new NumberFormatException("Infinite or NaN");
			if(multiplicator <= 0)
				throw new IllegalArgumentException("Multiplicator negative or 0");
			this.minorTickSpacing = minorTickSpacing;
			this.tickMultiplicator = multiplicator;
			setFormat();
			setTicks();
		}
		
		/**
		 * Returns the multiplicator used for determining the major tick spacing out of the minor tick spacing.
		 * @return Multiplicator for determining the major tick spacing
		 */
		public int getTickMultiplicator() {
			return tickMultiplicator;
		}

		/**
		 * Returns a format that can be used for generating String representations of tick values.
		 */
		public String getFormat() {
			return format;
		}
		
		/**
		 * Sets the format used for generating String representations of tick values.<br>
		 * For non-integer tick values this method uses a format with the same precision as a base value,
		 * which is set to the first tick in non zero-based operation mode and to minor tick spacing otherwise. 
		 */
		private void setFormat() {
			if(getMinorTickSpacing() == (int) getMinorTickSpacing()){
				format = "%.0f";
			} else {
//				BigDecimal dec = new BigDecimal(getMinorTickSpacing());
//				String s = String.valueOf(getMinorTickSpacing());
//				int precision = s.substring(s.lastIndexOf('.')+1).length();
//				format = FormatUtils.getFloatFormat(precision);
				format = FormatUtils.getFloatFormat(MathUtils.getRHD(minorTickSpacing));
			}
		}
		
	}