package jm.stockx;

import jm.stockx.dto.item.ItemDto;
import jm.stockx.dto.userPortfolio.BuyingDto;
import jm.stockx.entity.Item;

import java.util.Set;

public interface ItemService {

    Set<Item> getAll();

    ItemDto getItemDtoById(Long id);

    void create(Item item);

    void update(Item item);

    void delete(Long id);

    void buyItem(BuyingDto buyingDto);

    boolean isItemExist(Long id);

    ItemDto getItemDtoByItemName(String name);

    ItemDto getItemDtoByItemId(Long id);

    Item getItemByName(String name);

    Item getItemById(Long id);
}
