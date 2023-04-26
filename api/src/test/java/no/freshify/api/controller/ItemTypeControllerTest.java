package no.freshify.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.freshify.api.exception.ItemTypesNotFoundException;
import no.freshify.api.model.*;
import no.freshify.api.service.ItemTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ItemTypeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemTypeService itemTypeService;

    private ItemType itemType;
    private List<ItemType> itemTypeList;

    @BeforeEach
    public void setup() {
        itemType = new ItemType();
        itemType.setId(1L);
        itemType.setName("Test Item Type");

        itemTypeList = new ArrayList<>();
        itemTypeList.add(itemType);
    }

    @Test
    public void testSearchItemTypes_Success() throws Exception {
        when(itemTypeService.searchItemTypes(anyString())).thenReturn(itemTypeList);

        mockMvc.perform(get("/itemtype")
                        .param("name", "Test Item Type")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemType.getId()))
                .andExpect(jsonPath("$[0].name").value(itemType.getName()));

        verify(itemTypeService, times(1)).searchItemTypes(anyString());
    }

    @Test
    public void testSearchItemTypes_NotFound() throws Exception {
        when(itemTypeService.searchItemTypes(anyString())).thenThrow(new ItemTypesNotFoundException());

        mockMvc.perform(get("/itemtype")
                        .param("name", "Unknown Item Type")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemTypeService, times(1)).searchItemTypes(anyString());
    }
}
