package com.example.api.controller;

import com.example.api.dto.CursorPagedResult;
import com.example.api.model.Item;
import com.example.api.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/items")
@Tag(name = "Items", description = "Endpoints for managing items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @Operation(summary = "Create a new item")
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) {
        Item createdItem = itemService.createItem(item);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdItem.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdItem);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an item by ID")
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItem(id));
    }

    @GetMapping
    @Operation(summary = "Get a cursor-paginated list of items")
    public ResponseEntity<CursorPagedResult<Item>> getItems(
            @Parameter(description = "The cursor for the next page") @RequestParam(required = false) Long cursor,
            @Parameter(description = "Maximum number of items to return") @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(itemService.getItems(cursor, limit));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing item")
    public ResponseEntity<Item> updateItem(
            @PathVariable Long id, 
            @Valid @RequestBody Item item) {
        return ResponseEntity.ok(itemService.updateItem(id, item));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an item by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
