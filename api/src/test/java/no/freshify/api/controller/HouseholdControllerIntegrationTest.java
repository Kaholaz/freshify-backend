package no.freshify.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.freshify.api.model.Household;
import no.freshify.api.model.HouseholdMemberRole;
import no.freshify.api.model.dto.CreateHousehold;
import no.freshify.api.model.dto.HouseholdDTO;
import no.freshify.api.model.dto.UserFull;
import no.freshify.api.repository.HouseholdRepository;
import no.freshify.api.service.HouseholdService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class HouseholdControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    HouseholdService householdService;

    @Autowired
    private HouseholdRepository householdRepository;

    @Test
    public void createHousehold() throws Exception {
        CreateHousehold createHousehold = new CreateHousehold();
        createHousehold.setName("Test Household");

        MvcResult mvcResult = mockMvc.perform(post("/household")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createHousehold)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Household")))
                .andReturn();

        verify(householdService, VerificationModeFactory.times(1)).addHousehold(Mockito.any());

        HouseholdDTO householdDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), HouseholdDTO.class);

        assertEquals(householdDTO.getName(), createHousehold.getName());

        Optional<Household> optionalHousehold = householdRepository.findById(householdDTO.getId());
        assertTrue(optionalHousehold.isPresent());
        assertEquals(createHousehold.getName(), optionalHousehold.get().getName());
        assertEquals(1, optionalHousehold.get().getHouseholdMembers().size());
        assertEquals(HouseholdMemberRole.SUPERUSER, optionalHousehold.get().getHouseholdMembers().iterator().next().getRole());

        reset(householdService);
    }

    @Test
    public void deleteHouseholdTest() throws Exception {
        long householdId = 1L;
        when(householdService.findHouseholdByHouseholdId(householdId)).thenReturn(new Household());
        when(householdService.removeHousehold(anyLong())).thenReturn(ResponseEntity.noContent().build());
        mockMvc.perform(delete("/household/{id}", householdId))
                .andExpect(status().isNoContent());

        verify(householdService, VerificationModeFactory.times(1)).removeHousehold(Mockito.any());
        reset(householdService);
    }

    @Test
    public void getHouseholdByIdTest() throws Exception {
        long householdId = 1L;
        Household household = new Household();
        household.setId(householdId);
        when(householdService.findHouseholdByHouseholdId(householdId)).thenReturn(household);
        mockMvc.perform(get("/household/{id}", householdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) householdId)));

        verify(householdService, VerificationModeFactory.times(1)).findHouseholdByHouseholdId(Mockito.any());
        reset(householdService);
    }

    @Test
    public void getUsersTest() throws Exception {
        long householdId = 1L;
        List<UserFull> users = new ArrayList<>();
        users.add(new UserFull());
        when(householdService.getUsers(householdId)).thenReturn(users);
        mockMvc.perform(get("/household/{id}/users", householdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(householdService, VerificationModeFactory.times(1)).getUsers(Mockito.anyLong());
        reset(householdService);
    }

    @Test
    public void updateHouseholdTest() throws Exception {
        long householdId = 1L;
        HouseholdDTO household = new HouseholdDTO();
        household.setName("New Household Name");
        Household _household = new Household();
        _household.setName("Old Household Name");
        when(householdService.findHouseholdByHouseholdId(householdId)).thenReturn(_household);
        mockMvc.perform(put("/household/{id}", householdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(household)))
                .andExpect(status().isOk());
        assertEquals(household.getName(), _household.getName());

        verify(householdService, VerificationModeFactory.times(1)).findHouseholdByHouseholdId(Mockito.any());
        reset(householdService);
    }
}
