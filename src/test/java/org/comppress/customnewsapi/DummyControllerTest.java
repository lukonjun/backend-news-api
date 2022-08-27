package org.comppress.customnewsapi;

import org.comppress.customnewsapi.controller.DummyController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DummyControllerTest.class)
public class DummyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DummyController dummyController;

    @Test
    void testMessage() throws Exception {
        mockMvc.perform(get("/dummy/message")).andExpect(status().isOk());
    }

}
