package edu.utah.cs.uparknow;

import java.util.Arrays;
import java.util.Optional;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; 
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import edu.utah.cs.uparknow.model.Locations;
import edu.utah.cs.uparknow.repository.LocationsRepository;

@SpringBootTest
@AutoConfigureMockMvc
class LocationsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
       
    @MockBean
    private LocationsRepository locationsRepository;

    @Test
    void testGetAllLocations() throws Exception {

        Locations loc1 = new Locations();
        loc1.setPlaceId(1);
        loc1.setPlaceName("TestName1");
        loc1.setPlaceNameAbv("TN1");
        loc1.setPlaceLat(40.123);
        loc1.setPlaceLon(-111.123);

        Locations loc2 = new Locations();
        loc2.setPlaceId(2);
        loc2.setPlaceName("TestName2");
        loc2.setPlaceNameAbv("TN2");
        loc2.setPlaceLat(41.0);
        loc2.setPlaceLon(-111.0);

        Mockito.when(locationsRepository.findAll())
               .thenReturn(Arrays.asList(loc1, loc2));

        mockMvc.perform(get("/api/v1/locations"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].Place_Name", is("TestName1")))
               .andExpect(jsonPath("$[1].Place_Name", is("TestName2")));
    }

    @Test
    void testGetLocationById() throws Exception {
        Locations loc = new Locations();
        loc.setPlaceId(10);
        loc.setPlaceName("SingleLocation");
        loc.setPlaceNameAbv("SL");
        loc.setPlaceLat(40.5);
        loc.setPlaceLon(-111.5);

        Mockito.when(locationsRepository.findById(10))
               .thenReturn(Optional.of(loc));

        mockMvc.perform(get("/api/v1/locations/10"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.Place_ID", is(10)))
               .andExpect(jsonPath("$.Place_Name", is("SingleLocation")));
    }

    @Test
    void testGetLocationById_NotFound() throws Exception {
        Mockito.when(locationsRepository.findById(999))
               .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/locations/999"))
               .andExpect(status().isNotFound());
    }


    @SuppressWarnings("unused")
    @Test
    void testCreateLocation() throws Exception {
        String jsonBody = """
        {
            "placeId": 100,
            "placeName": "NewLocation",
            "placeNameAbv": "NLoc",
            "placeLat": 40.999,
            "placeLon": -111.999
        }
        """;

        Mockito.when(locationsRepository.save(Mockito.any(Locations.class)))
               .thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/api/v1/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk());

        ArgumentCaptor<Locations> captor = ArgumentCaptor.forClass(Locations.class);
        Mockito.verify(locationsRepository).save(captor.capture());
        Locations saved = captor.getValue();
    }

    @SuppressWarnings("unused")
    @Test
    void testUpdateLocation() throws Exception {
        Locations oldLoc = new Locations();
        oldLoc.setPlaceId(50);
        oldLoc.setPlaceName("OldName");
        oldLoc.setPlaceNameAbv("ON");
        oldLoc.setPlaceLat(40.0);
        oldLoc.setPlaceLon(-111.0);

        Mockito.when(locationsRepository.findById(50))
               .thenReturn(Optional.of(oldLoc));

        Mockito.when(locationsRepository.save(Mockito.any(Locations.class)))
               .thenAnswer(i -> i.getArgument(0));

        String updateJson = """
        {
            "placeName": "UpdatedName",
            "placeNameAbv": "UN",
            "placeLat": 41.0,
            "placeLon": -112.0
        }
        """;

        mockMvc.perform(put("/api/v1/locations/50")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk());

        ArgumentCaptor<Locations> captor = ArgumentCaptor.forClass(Locations.class);
        Mockito.verify(locationsRepository).save(captor.capture());
        Locations saved = captor.getValue();
    }

    @Test
    void testDeleteLocation() throws Exception {
        Locations existing = new Locations();
        existing.setPlaceId(60);
        Mockito.when(locationsRepository.findById(60))
               .thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/api/v1/locations/60"))
               .andExpect(status().isNoContent());

        Mockito.verify(locationsRepository).delete(existing);
    }
}
