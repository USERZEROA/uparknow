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

/**
 * 测试 ParkingSpacesService 的业务逻辑
 * 使用 @MockBean 来模拟 ParkingSpacesRepository，不触碰真实数据库
 */
@SpringBootTest
class ParkingSpacesServiceTest {

    @Autowired
    private ParkingSpacesService parkingSpacesService;

    @SuppressWarnings("removal")
    @MockBean
    private ParkingSpacesRepository parkingSpacesRepository;

    @Test
    void testGetAllParkingSpaces() {
        // 准备一个 Mock 数据
        ParkingSpaces ps = new ParkingSpaces();
        ps.setSpace_ID(100);
        ps.setSpace_Row(10);
        ps.setSpace_Column(20);
        ps.setSpace_Parked(true);

        Mockito.when(parkingSpacesRepository.findAll())
               .thenReturn(Collections.singletonList(ps));

        // 调用 Service
        List<ParkingSpaces> result = parkingSpacesService.getAllParkingSpaces();
        // 断言
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(100, result.get(0).getSpace_ID().intValue());
        Assertions.assertTrue(result.get(0).getSpace_Parked());
    }

    @Test
    void testCreateParkingSpace() {
        // 准备要创建的 ParkingSpaces
        ParkingSpaces newSpace = new ParkingSpaces();
        newSpace.setSpace_Row(3);
        newSpace.setSpace_Column(4);
        newSpace.setSpace_Parked(false);

        // Mock save(...) 直接回传
        Mockito.when(parkingSpacesRepository.save(Mockito.any(ParkingSpaces.class)))
               .thenAnswer(inv -> {
                   ParkingSpaces arg = inv.getArgument(0);
                   arg.setSpace_ID(999); // 模拟数据库自动生成ID
                   return arg;
               });

        ParkingSpaces created = parkingSpacesService.createParkingSpace(newSpace);

        // 验证
        Assertions.assertNotNull(created);
        Assertions.assertEquals(999, created.getSpace_ID().intValue());
        Assertions.assertEquals(3, created.getSpace_Row().intValue());
        Assertions.assertFalse(created.getSpace_Parked());

        // 验证 repository.save() 调用
        ArgumentCaptor<ParkingSpaces> captor = ArgumentCaptor.forClass(ParkingSpaces.class);
        Mockito.verify(parkingSpacesRepository).save(captor.capture());
        ParkingSpaces savedObj = captor.getValue();
        Assertions.assertEquals(3, savedObj.getSpace_Row().intValue());
        Assertions.assertEquals(4, savedObj.getSpace_Column().intValue());
    }

    @Test
    void testUpdateParkingSpace() {
        // 假设数据库里已有 ID=1 的车位
        ParkingSpaces existing = new ParkingSpaces();
        existing.setSpace_ID(1);
        existing.setSpace_Row(1);
        existing.setSpace_Column(1);
        existing.setSpace_Parked(false);

        Mockito.when(parkingSpacesRepository.findById(1))
                .thenReturn(Optional.of(existing));

        Mockito.when(parkingSpacesRepository.save(Mockito.any(ParkingSpaces.class)))
                .thenAnswer(i -> i.getArgument(0));

        // 要更新为 row=2, col=2, parked=true
        ParkingSpaces updateDetails = new ParkingSpaces();
        updateDetails.setSpace_Row(2);
        updateDetails.setSpace_Column(2);
        updateDetails.setSpace_Parked(true);

        // 调用 Service
        ParkingSpaces updated = parkingSpacesService.updateParkingSpace(1, updateDetails);

        // 验证更新结果
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
        // 已存在 ID=10
        ParkingSpaces existing = new ParkingSpaces();
        existing.setSpace_ID(10);

        Mockito.when(parkingSpacesRepository.findById(10))
               .thenReturn(Optional.of(existing));

        parkingSpacesService.deleteParkingSpace(10);

        // 验证 delete() 调用
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
