package com.website.service.mapper;

import com.website.domain.Track;

public class TrackMapper
{
    public static Track TrackDTOToTrack(String trackDTO, String separator)
    {
        if (trackDTO == null)
        {
            return null;
        }
        else
        {
        	String[] columns = trackDTO.trim().split(separator); 
            Track track = new Track();
            track.setName(columns[0].trim());//first column of passed csv is name
            track.setBpm(Integer.valueOf(columns[1].trim()));//second column of passed csv is bpm//IMPROVE:type error not handled
            return track;
        }
    }

}
