package com.website.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.website.domain.Track;
import com.website.repository.TrackRepository;
import com.website.service.mapper.TrackMapper;
import com.website.web.rest.errors.BadRequestAlertException;
import com.website.web.rest.util.HeaderUtil;
import com.website.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Track.
 */
@RestController
@RequestMapping("/api")
public class TrackResource {

    private final Logger log = LoggerFactory.getLogger(TrackResource.class);

    private static final String ENTITY_NAME = "track";

    private final TrackRepository trackRepository;

    public TrackResource(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    /**
     * POST  /tracks : Create a new track.
     *
     * @param track the track to create
     * @return the ResponseEntity with status 201 (Created) and with body the new track, or with status 400 (Bad Request) if the track has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tracks")
    @Timed
    public ResponseEntity<Track> createTrack(@Valid @RequestBody Track track) throws URISyntaxException
    {
    	log.debug("Track JSON is "+new Gson().toJson(track));
        log.debug("REST request to save Track : {}", track);
        if (track.getId() != null) {
            throw new BadRequestAlertException("A new track cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Track result = trackRepository.save(track);
        return ResponseEntity.created(new URI("/api/tracks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
    /**
     * POST  /tracks/batch : Create new tracks thru a csv file upload
     *
     * @param csvFile the uplaoded csvFile
     * @return the ResponseEntity with status 201 (Created) and with body the new track, or with status 400 (Bad Request) if the track has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tracks/batchCsv")
    @Timed
    public ResponseEntity<List<Track>> createTrack(@Valid @RequestParam("file") MultipartFile csvFile,Pageable pageable) throws URISyntaxException, IOException
    {
    	String separator=",";//HARDCODED: should be passed as parameter
    	//IMPROVE:quotes and special characters not handled
    	InputStream is = csvFile.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
    	String line=null;
    	List<Track> tracks=new ArrayList<Track>();
    	int i=0;
    	br.readLine();//the first line contains the headers, skip it
    	while((line=br.readLine())!=null)
    	{
    		Track track=TrackMapper.TrackDTOToTrack(line, separator);
    		tracks.add(track);
            if (track.getId() != null)
            {
                throw new BadRequestAlertException("A new track cannot already have an ID", ENTITY_NAME, "idexists."
                		+ " Csv Line "+(++i));
            }
            Track result = trackRepository.save(track);
    	}
    	br.close();
    	return getAllTracks(pageable);
        /*return ResponseEntity.created(new URI("/api/tracks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);*/
    }

    /**
     * PUT  /tracks : Updates an existing track.
     *
     * @param track the track to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated track,
     * or with status 400 (Bad Request) if the track is not valid,
     * or with status 500 (Internal Server Error) if the track couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tracks")
    @Timed
    public ResponseEntity<Track> updateTrack(@Valid @RequestBody Track track) throws URISyntaxException
    {
        log.debug("REST request to update Track : {}", track);
        if (track.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Track result = trackRepository.save(track);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, track.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tracks : get all the tracks.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of tracks in body
     */
    @GetMapping("/tracks")
    @Timed
    public ResponseEntity<List<Track>> getAllTracks(Pageable pageable)
    {
    	log.debug("pageable is "+new Gson().toJson(pageable));
        log.debug("REST request to get a page of Tracks");
        Page<Track> page = trackRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tracks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tracks/:id : get the "id" track.
     *
     * @param id the id of the track to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the track, or with status 404 (Not Found)
     */
    @GetMapping("/tracks/{id}")
    @Timed
    public ResponseEntity<Track> getTrack(@PathVariable Long id) {
        log.debug("REST request to get Track : {}", id);
        Optional<Track> track = trackRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(track);
    }

    /**
     * DELETE  /tracks/:id : delete the "id" track.
     *
     * @param id the id of the track to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tracks/{id}")
    @Timed
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        log.debug("REST request to delete Track : {}", id);

        trackRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
