package edu.utah.cs.uparknow;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import edu.utah.cs.uparknow.exception.ResourceNotFoundException;
import edu.utah.cs.uparknow.model.ParkingSpaces;
import edu.utah.cs.uparknow.repository.ParkingSpacesRepository;
import edu.utah.cs.uparknow.service.ParkingSpacesService;

@SpringBootTest
class ParkingSpacesServiceTest {

    @Autowired
    private ParkingSpacesService parkingSpacesService;

    @SuppressWarnings("removal")
    @MockBean
    private ParkingSpacesRepository parkingSpacesRepository;

    @Test
    void testGetAllParkingSpaces() {
        ParkingSpaces ps = new ParkingSpaces();
        ps.setSpace_ID(100);
        ps.setSpace_Row(10);
        ps.setSpace_Column(20);
        ps.setSpace_Parked(true);

        Mockito.when(parkingSpacesRepository.findAll())
               .thenReturn(Collections.singletonList(ps));

        List<ParkingSpaces> result = parkingSpacesService.getAllParkingSpaces();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(100, result.get(0).getSpace_ID().intValue());
        Assertions.assertTrue(result.get(0).getSpace_Parked());
    }

    @Test
    void testCreateParkingSpace() {
        ParkingSpaces newSpace = new ParkingSpaces();
        newSpace.setSpace_Row(3);
        newSpace.setSpace_Column(4);
        newSpace.setSpace_Parked(false);

        Mockito.when(parkingSpacesRepository.save(Mockito.any(ParkingSpaces.class)))
               .thenAnswer(inv -> {
                   ParkingSpaces arg = inv.getArgument(0);
                   arg.setSpace_ID(999);
                   return arg;
               });

        ParkingSpaces created = parkingSpacesService.createParkingSpace(newSpace);
        Assertions.assertNotNull(created);
        Assertions.assertEquals(999, created.getSpace_ID().intValue());
        Assertions.assertEquals(3, created.getSpace_Row().intValue());
        Assertions.assertFalse(created.getSpace_Parked());

        ArgumentCaptor<ParkingSpaces> captor = ArgumentCaptor.forClass(ParkingSpaces.class);
        Mockito.verify(parkingSpacesRepository).save(captor.capture());
        ParkingSpaces savedObj = captor.getValue();
        Assertions.assertEquals(3, savedObj.getSpace_Row().intValue());
        Assertions.assertEquals(4, savedObj.getSpace_Column().intValue());
    }

    @Test
    void testUpdateParkingSpace() {
        ParkingSpaces existing = new ParkingSpaces();
        existing.setSpace_ID(1);
        existing.setSpace_Row(1);
        existing.setSpace_Column(1);
        existing.setSpace_Parked(false);

        Mockito.when(parkingSpacesRepository.findById(1))
                .thenReturn(Optional.of(existing));

        Mockito.when(parkingSpacesRepository.save(Mockito.any(ParkingSpaces.class)))
                .thenAnswer(i -> i.getArgument(0));

        ParkingSpaces updateDetails = new ParkingSpaces();
        updateDetails.setSpace_Row(2);
        updateDetails.setSpace_Column(2);
        updateDetails.setSpace_Parked(true);

        ParkingSpaces updated = parkingSpacesService.updateParkingSpace(1, updateDetails);
        Assertions.assertEquals(2, updated.getSpace_Row().intValue());
        Assertions.assertEquals(2, updated.getSpace_Column().intValue());
        Assertions.assertTrue(updated.getSpace_Parked());
    }

    @Test
    void testUpdateParkingSpace_NotFound() {
        Mockito.when(parkingSpacesRepository.findById(999))
            .thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(
            ResourceNotFoundException.class,
            () -> parkingSpacesService.updateParkingSpace(999, new ParkingSpaces())
        );

        Assertions.assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void testDeleteParkingSpace() {
        ParkingSpaces existing = new ParkingSpaces();
        existing.setSpace_ID(10);

        Mockito.when(parkingSpacesRepository.findById(10))
               .thenReturn(Optional.of(existing));

        parkingSpacesService.deleteParkingSpace(10);
        Mockito.verify(parkingSpacesRepository).delete(existing);
    }

    @Test
    void testDeleteParkingSpace_NotFound() {
        Mockito.when(parkingSpacesRepository.findById(999))
               .thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(
        ResourceNotFoundException.class,
        () -> parkingSpacesService.deleteParkingSpace(999));
        Assertions.assertTrue(ex.getMessage().contains("not found"));
    }
}
