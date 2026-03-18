package com.example.api.service;

import com.example.api.dto.CursorPagedResult;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Item;
import com.example.api.repository.ItemRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    public Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    public CursorPagedResult<Item> getItems(Long cursor, int limit) {
        long effectiveCursor = (cursor != null) ? cursor : 0L;
        // Fetch limit + 1 to determine if there is a next page
        List<Item> items = itemRepository.findItemsAfterCursor(effectiveCursor, PageRequest.of(0, limit + 1));
        
        boolean hasNext = items.size() > limit;
        List<Item> resultData = hasNext ? items.subList(0, limit) : items;
        
        String nextCursor = null;
        if (!resultData.isEmpty() && hasNext) {
            nextCursor = String.valueOf(resultData.get(resultData.size() - 1).getId());
        }

        return new CursorPagedResult<>(resultData, nextCursor, hasNext);
    }

    public Item updateItem(Long id, Item updatedItem) {
        Item existingItem = getItem(id);
        existingItem.setName(updatedItem.getName());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setPrice(updatedItem.getPrice());
        return itemRepository.save(existingItem);
    }

    public void deleteItem(Long id) {
        Item item = getItem(id);
        itemRepository.delete(item);
    }
}
