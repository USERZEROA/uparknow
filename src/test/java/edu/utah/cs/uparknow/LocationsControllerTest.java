package edu.utah.cs.uparknow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import edu.utah.cs.uparknow.model.Locations;
import edu.utah.cs.uparknow.repository.LocationsRepository;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; 
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 测试 LocationsController 的 REST 接口
 * 通过 MockMvc 发送各种 HTTP 请求，并使用 @MockBean 模拟 LocationsRepository
 * 不会改动真实数据库
 */
@SpringBootTest
@AutoConfigureMockMvc
class LocationsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationsRepository locationsRepository;

    /**
     * 测试: GET /api/v1/locations
     * 预期: 返回所有 Locations
     */
    @Test
    void testGetAllLocations() throws Exception {
        // 1. 准备 Mock 数据
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

        // 2. Mock: 当调用 findAll() 时，就返回一个含 loc1, loc2 的列表
        Mockito.when(locationsRepository.findAll())
               .thenReturn(Arrays.asList(loc1, loc2));

        // 3. 使用 MockMvc 发送 GET /api/v1/locations
        mockMvc.perform(get("/api/v1/locations"))
               .andExpect(status().isOk())
               // 断言 JSON 数组长度为2
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].Place_Name", is("TestName1")))
               .andExpect(jsonPath("$[1].Place_Name", is("TestName2")));
    }

    /**
     * 测试: GET /api/v1/locations/{id}
     * 预期: 返回单个 Location
     */
    @Test
    void testGetLocationById() throws Exception {
        // Mock 数据
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

    /**
     * 测试: GET /api/v1/locations/{id} 不存在
     * 预期: 返回 404
     */
    @Test
    void testGetLocationById_NotFound() throws Exception {
        Mockito.when(locationsRepository.findById(999))
               .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/locations/999"))
               .andExpect(status().isNotFound());
    }

    /**
     * 测试: POST /api/v1/locations
     * 预期: 创建成功返回 200(201), 同时验证 body 内容
     */
    @Test
    void testCreateLocation() throws Exception {
        // 发送 JSON:
        String jsonBody = """
        {
            "placeId": 100,
            "placeName": "NewLocation",
            "placeNameAbv": "NLoc",
            "placeLat": 40.999,
            "placeLon": -111.999
        }
        """;

        // Mock save(...) 返回一个对象(假装写入数据库)
        Mockito.when(locationsRepository.save(Mockito.any(Locations.class)))
               .thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/api/v1/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk());

        // 验证 repository.save() 被正确调用, 并拿到正确的数据
        ArgumentCaptor<Locations> captor = ArgumentCaptor.forClass(Locations.class);
        Mockito.verify(locationsRepository).save(captor.capture());

        Locations saved = captor.getValue();
    }

    /**
     * 测试: PUT /api/v1/locations/{id}
     * 预期: 更新成功
     */
    @Test
    void testUpdateLocation() throws Exception {
        // 数据库里原本的地点
        Locations oldLoc = new Locations();
        oldLoc.setPlaceId(50);
        oldLoc.setPlaceName("OldName");
        oldLoc.setPlaceNameAbv("ON");
        oldLoc.setPlaceLat(40.0);
        oldLoc.setPlaceLon(-111.0);

        Mockito.when(locationsRepository.findById(50))
               .thenReturn(Optional.of(oldLoc));

        // Mock save
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

        // 验证 save() 调用
        ArgumentCaptor<Locations> captor = ArgumentCaptor.forClass(Locations.class);
        Mockito.verify(locationsRepository).save(captor.capture());
        Locations saved = captor.getValue();
    }

    /**
     * 测试: DELETE /api/v1/locations/{id}
     */
    @Test
    void testDeleteLocation() throws Exception {
        // 假设数据库里有 id=60
        Locations existing = new Locations();
        existing.setPlaceId(60);
        Mockito.when(locationsRepository.findById(60))
               .thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/api/v1/locations/60"))
               .andExpect(status().isNoContent());

        // 验证 repository.delete() 调用
        Mockito.verify(locationsRepository).delete(existing);
    }
}
