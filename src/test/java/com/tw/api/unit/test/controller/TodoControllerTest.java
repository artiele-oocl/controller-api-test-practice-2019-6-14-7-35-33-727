package com.tw.api.unit.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import com.tw.api.unit.test.services.ShowService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
public class TodoControllerTest {
    @Autowired
    private TodoController todoController;
    @MockBean
    private TodoRepository todoRepository;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll() throws Exception {
        //given
        List<Todo> todos = new ArrayList<>();
        Todo todo = new Todo(1, "sample title", false, 2);
        todos.add(todo);
        when(todoRepository.getAll()).thenReturn(todos);
        //when
        ResultActions result = mvc.perform(get("/todos"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("sample title"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[0].order").value(2))
        ;
    }
    @Test
    void getTodo() throws Exception {
        //given
        List<Todo> todos = new ArrayList<>();
        Todo todo1 = new Todo(1, "sample title", false, 2);
        Todo todo2 = new Todo(2, "another sample title", true, 5);
        todos.add(todo1);
        todos.add(todo2);
        when(todoRepository.findById(2)).thenReturn(java.util.Optional.ofNullable(todos.get(1)));
        //when
        ResultActions result = mvc.perform(get("/todos/2"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("another sample title"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.order").value(5))
        ;
    }
//    @Test
//    void saveTodo() throws Exception {
//        //given
//        Todo todo = new Todo(1, "sample title", false, 2);
//
//        todoRepository.add(todo);
//        when(todoRepository.add(todo)).thenReturn(todo);
//        //when
//        ResultActions result = mvc.perform(post("/todos"));
//        //then
//        result.andExpect(status().isOk())
//                .andDo(print())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.title").value("sample title"))
//                .andExpect(jsonPath("$.completed").value(false))
//                .andExpect(jsonPath("$.order").value(2))
//        ;
//    }
    @Test
    void deleteOneTodo() throws Exception {
        //given
        Todo todo = new Todo(1, "sample title", false, 2);
        todoRepository.add(todo);
        when(todoRepository.findById(1)).thenReturn(java.util.Optional.of(todo));
        //when
        ResultActions result = mvc.perform(delete("/todos/1"));
        //then
        result.andExpect(status().isOk())
        ;
    }
    @Test
    void updateTodo() throws Exception {
        //given
        Todo todo = new Todo(1, "sample title", false, 2);
        Todo newTodo = new Todo(1, "Edited title", false, 2);
        todoRepository.add(todo);
        Optional<Todo> optionalTodo = Optional.of(todo);
        when(todoRepository.findById(1)).thenReturn(optionalTodo);
        todoRepository.delete(optionalTodo.get());

        todoRepository.add(newTodo);
        //when
        ResultActions result = mvc.perform(patch("/todos/1").contentType("application/json").content(objectMapper.writeValueAsString(newTodo)));
        //then
        result.andExpect(status().isOk())
        ;
    }
}
