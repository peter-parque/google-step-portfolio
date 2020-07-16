// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.*;

public final class FindMeetingQuery {

    /**
    * Returns a set of times in a day when a specified meeting could happen, given 
    * known events during that day.
    */
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<TimeRange> includeOptional = query(events, request, true);
        if (includeOptional.size() == 0) {
            return query(events, request, false);
        } else {
            return includeOptional;
        }
    }

    /**
    * Returns a set of times in a day when a specified meeting could happen, given 
    * known events during that day. Option to include optional attendees.
    */
    private Collection<TimeRange> query(Collection<Event> events, 
        MeetingRequest request, 
        boolean includeOptional) {

        List<TimeRange> relevantTimes = removeIrrelevant(events, request, includeOptional);

        Collections.sort(relevantTimes, TimeRange.ORDER_BY_START);

        Collection<TimeRange> openTimes = new ArrayList<>();
        int intervalStart = TimeRange.START_OF_DAY;

        for (TimeRange range : relevantTimes) {
            if (range.start() > intervalStart &&
                range.start()-intervalStart >= request.getDuration()) {

                openTimes.add(TimeRange.fromStartEnd(intervalStart, range.start(), false));
            }
            if (range.end() > intervalStart) {
                intervalStart = range.end();
            }
        }
        if (TimeRange.END_OF_DAY > intervalStart &&
            TimeRange.END_OF_DAY-intervalStart >= request.getDuration()) {

            openTimes.add(TimeRange.fromStartEnd(intervalStart, TimeRange.END_OF_DAY, true));
        }

        return openTimes;
    }

    /**
    * Takes a set of meetings and a list of people, and returns a list of only the timeranges 
    * from those meetings where those people are busy. Option to include optional attendees.
    */
    private List<TimeRange> removeIrrelevant(Collection<Event> events, 
        MeetingRequest request, 
        boolean includeOptional) {

        List<TimeRange> relevantTimes = new ArrayList<>();
        for (Event event : events) {
            Collection<String> eventAttendees = event.getAttendees();
            for (String attendee : request.getAttendees()) {
                if (eventAttendees.contains(attendee)) {
                    relevantTimes.add(event.getWhen());
                    break;
                }
            }
            if (includeOptional) {
                for (String optionalAttendee : request.getOptionalAttendees()) {
                    if (eventAttendees.contains(optionalAttendee)) {
                        relevantTimes.add(event.getWhen());
                        break;
                    }
                }
            }
        }
        return relevantTimes;
    }
}
