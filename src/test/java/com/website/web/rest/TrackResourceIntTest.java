package com.website.web.rest;

import com.website.CatalogApp;

import com.website.domain.Track;
import com.website.repository.TrackRepository;
import com.website.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;


import static com.website.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TrackResource REST controller.
 *
 * @see TrackResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CatalogApp.class)
public class TrackResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_BPM = 1;
    private static final Integer UPDATED_BPM = 2;

    @Autowired
    private TrackRepository trackRepository;


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTrackMockMvc;

    private Track track;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TrackResource trackResource = new TrackResource(trackRepository);
        this.restTrackMockMvc = MockMvcBuilders.standaloneSetup(trackResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Track createEntity(EntityManager em) {
        Track track = new Track()
            .name(DEFAULT_NAME)
            .bpm(DEFAULT_BPM);
        return track;
    }

    @Before
    public void initTest() {
        track = createEntity(em);
    }

    @Test
    @Transactional
    public void createTrack() throws Exception {
        int databaseSizeBeforeCreate = trackRepository.findAll().size();

        // Create the Track
        restTrackMockMvc.perform(post("/api/tracks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(track)))
            .andExpect(status().isCreated());

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll();
        assertThat(trackList).hasSize(databaseSizeBeforeCreate + 1);
        Track testTrack = trackList.get(trackList.size() - 1);
        assertThat(testTrack.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTrack.getBpm()).isEqualTo(DEFAULT_BPM);
    }

    @Test
    @Transactional
    public void createTrackWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = trackRepository.findAll().size();

        // Create the Track with an existing ID
        track.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTrackMockMvc.perform(post("/api/tracks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(track)))
            .andExpect(status().isBadRequest());

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll();
        assertThat(trackList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackRepository.findAll().size();
        // set the field null
        track.setName(null);

        // Create the Track, which fails.

        restTrackMockMvc.perform(post("/api/tracks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(track)))
            .andExpect(status().isBadRequest());

        List<Track> trackList = trackRepository.findAll();
        assertThat(trackList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTracks() throws Exception {
        // Initialize the database
        trackRepository.saveAndFlush(track);

        // Get all the trackList
        restTrackMockMvc.perform(get("/api/tracks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(track.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].bpm").value(hasItem(DEFAULT_BPM)));
    }
    

    @Test
    @Transactional
    public void getTrack() throws Exception {
        // Initialize the database
        trackRepository.saveAndFlush(track);

        // Get the track
        restTrackMockMvc.perform(get("/api/tracks/{id}", track.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(track.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.bpm").value(DEFAULT_BPM));
    }
    @Test
    @Transactional
    public void getNonExistingTrack() throws Exception {
        // Get the track
        restTrackMockMvc.perform(get("/api/tracks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTrack() throws Exception {
        // Initialize the database
        trackRepository.saveAndFlush(track);

        int databaseSizeBeforeUpdate = trackRepository.findAll().size();

        // Update the track
        Track updatedTrack = trackRepository.findById(track.getId()).get();
        // Disconnect from session so that the updates on updatedTrack are not directly saved in db
        em.detach(updatedTrack);
        updatedTrack
            .name(UPDATED_NAME)
            .bpm(UPDATED_BPM);

        restTrackMockMvc.perform(put("/api/tracks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTrack)))
            .andExpect(status().isOk());

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll();
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate);
        Track testTrack = trackList.get(trackList.size() - 1);
        assertThat(testTrack.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTrack.getBpm()).isEqualTo(UPDATED_BPM);
    }

    @Test
    @Transactional
    public void updateNonExistingTrack() throws Exception {
        int databaseSizeBeforeUpdate = trackRepository.findAll().size();

        // Create the Track

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTrackMockMvc.perform(put("/api/tracks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(track)))
            .andExpect(status().isBadRequest());

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll();
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTrack() throws Exception {
        // Initialize the database
        trackRepository.saveAndFlush(track);

        int databaseSizeBeforeDelete = trackRepository.findAll().size();

        // Get the track
        restTrackMockMvc.perform(delete("/api/tracks/{id}", track.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Track> trackList = trackRepository.findAll();
        assertThat(trackList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Track.class);
        Track track1 = new Track();
        track1.setId(1L);
        Track track2 = new Track();
        track2.setId(track1.getId());
        assertThat(track1).isEqualTo(track2);
        track2.setId(2L);
        assertThat(track1).isNotEqualTo(track2);
        track1.setId(null);
        assertThat(track1).isNotEqualTo(track2);
    }
}
