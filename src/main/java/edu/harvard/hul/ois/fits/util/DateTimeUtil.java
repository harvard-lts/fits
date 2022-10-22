//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.util;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.TemporalAccessor;

/**
 * Utility class for working with timestamps
 */
public final class DateTimeUtil {

    private static final DateTimeFormatter STANDARD_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(DAY_OF_MONTH, 2)
            .optionalStart()
            .appendLiteral('T')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendFraction(NANO_OF_SECOND, 0, 9, true)
            .optionalStart()
            .appendOffset("+HH:MM", "Z")
            .toFormatter();

    private static final DateTimeFormatter DATE_TIME_WITH_SPACE = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME)
            .optionalStart()
            .appendOffsetId()
            .optionalStart()
            .appendLiteral('[')
            .parseCaseSensitive()
            .appendZoneRegionId()
            .appendLiteral(']')
            .toFormatter();

    private static final DateTimeFormatter DATE_TIME_WITH_COLON = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral(':')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral(':')
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME)
            .optionalStart()
            .appendOffsetId()
            .optionalStart()
            .appendLiteral('[')
            .parseCaseSensitive()
            .appendZoneRegionId()
            .appendLiteral(']')
            .toFormatter();

    private static final DateTimeFormatter DATE_WITH_COLON = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral(':')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral(':')
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter();

    private static final DateTimeFormatter DATE_TIME_WITH_PERIOD = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('.')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('.')
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME)
            .optionalStart()
            .appendOffsetId()
            .optionalStart()
            .appendLiteral('[')
            .parseCaseSensitive()
            .appendZoneRegionId()
            .appendLiteral(']')
            .toFormatter();

    private static final DateTimeFormatter DATE_WITH_PERIOD = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('.')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('.')
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter();

    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[] {
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ISO_DATE,
        DATE_TIME_WITH_SPACE,
        DateTimeFormatter.BASIC_ISO_DATE,
        DateTimeFormatter.RFC_1123_DATE_TIME,
        DATE_TIME_WITH_COLON,
        DATE_WITH_COLON,
        DATE_TIME_WITH_PERIOD,
        DATE_WITH_PERIOD
    };

    private DateTimeUtil() {
        // cannot instantiate
    }

    /**
     * Attempts to parse the specified timestamp and convert it to a standard format. Timezones are not inferred if
     * they are not specified. If the operation is successful, then the resulting timestamp will be in one of the
     * following formats:
     *
     * <ul>
     *     <li>yyyy-MM-dd</li>
     *     <li>yyyy-MM-dd'T'HH:mm:ss</li>
     *     <li>yyyy-MM-dd'T'HH:mm:ss'Z'</li>
     * </ul>
     *
     * @param originalTimestamp the timestamp to standardize
     * @return converted timestamp or the original value if it cannot be parsed
     */
    public static String standardize(String originalTimestamp) {
        originalTimestamp = originalTimestamp.trim();

        if (originalTimestamp.isEmpty()) {
            return originalTimestamp;
        }

        String standardized = originalTimestamp;

        TemporalAccessor parsed = null;

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                parsed = formatter.parseBest(
                        originalTimestamp,
                        OffsetDateTime::from,
                        ZonedDateTime::from,
                        LocalDateTime::from,
                        LocalDate::from);
                break;
            } catch (Exception e) {
                // not the right format -- try another
            }
        }

        if (parsed != null) {
            if (parsed instanceof OffsetDateTime) {
                OffsetDateTime offset = (OffsetDateTime) parsed;
                parsed = offset.withOffsetSameInstant(ZoneOffset.UTC);
            } else if (parsed instanceof ZonedDateTime) {
                ZonedDateTime zoned = (ZonedDateTime) parsed;
                parsed = zoned.withZoneSameInstant(ZoneOffset.UTC);
            }

            standardized = STANDARD_FORMAT.format(parsed);
        }

        return standardized;
    }
}
