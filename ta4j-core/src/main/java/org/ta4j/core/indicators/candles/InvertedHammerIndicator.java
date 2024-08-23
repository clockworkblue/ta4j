/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2023 Ta4j Organization & respective
 * authors (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ta4j.core.indicators.candles;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.trend.DownTrendIndicator;

/**
 * Inverted hammer candle indicator.
 */
public class InvertedHammerIndicator extends CachedIndicator<Boolean> {

    private static final double BODY_LENGTH_TO_BOTTOM_WICK_COEFFICIENT = 1d;
    private static final double BODY_LENGTH_TO_UPPER_WICK_COEFFICIENT = 2d;

    private final RealBodyIndicator realBodyIndicator;
    private final DownTrendIndicator trendIndicator;

    /**
     * Constructor.
     *
     * @param series the bar series
     */
    public InvertedHammerIndicator(final BarSeries series) {
        super(series);
        this.realBodyIndicator = new RealBodyIndicator(series);
        this.trendIndicator = new DownTrendIndicator(series);
    }

    @Override
    protected Boolean calculate(final int index) {
        final var bar = getBarSeries().getBar(index);
        final var openPrice = bar.getOpenPrice();
        final var closePrice = bar.getClosePrice();
        final var lowPrice = bar.getLowPrice();
        final var highPrice = bar.getHighPrice();

        final var bodyHeight = this.realBodyIndicator.getValue(index).abs();

        final var upperBodyBoundary = openPrice.max(closePrice);
        final var bottomBodyBoundary = openPrice.min(closePrice);
        final var bottomWickHeight = bottomBodyBoundary.minus(lowPrice);
        final var upperWickHeight = highPrice.minus(upperBodyBoundary);

        return upperWickHeight.dividedBy(bodyHeight).isGreaterThan(numOf(BODY_LENGTH_TO_UPPER_WICK_COEFFICIENT))
                && bottomWickHeight.dividedBy(bodyHeight)
                        .isLessThanOrEqual(numOf(BODY_LENGTH_TO_BOTTOM_WICK_COEFFICIENT))
                && this.trendIndicator.getValue(index);
    }

    @Override
    public int getUnstableBars() {
        return 0;
    }
}
