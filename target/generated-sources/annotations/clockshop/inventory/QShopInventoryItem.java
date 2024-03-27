package clockshop.inventory;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QShopInventoryItem is a Querydsl query type for ShopInventoryItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QShopInventoryItem extends EntityPathBase<ShopInventoryItem> {

    private static final long serialVersionUID = -782364643L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QShopInventoryItem shopInventoryItem = new QShopInventoryItem("shopInventoryItem");

    public final org.salespointframework.inventory.QMultiInventoryItem _super;

    public final clockshop.catalog.QArticle article;

    // inherited
    public final org.salespointframework.inventory.QInventoryItem_InventoryItemIdentifier inventoryItemIdentifier;

    public final BooleanPath nonSeller = createBoolean("nonSeller");

    // inherited
    public final org.salespointframework.catalog.QProduct product;

    // inherited
    public final org.salespointframework.quantity.QQuantity quantity;

    public final StringPath warehouseId = createString("warehouseId");

    public QShopInventoryItem(String variable) {
        this(ShopInventoryItem.class, forVariable(variable), INITS);
    }

    public QShopInventoryItem(Path<? extends ShopInventoryItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QShopInventoryItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QShopInventoryItem(PathMetadata metadata, PathInits inits) {
        this(ShopInventoryItem.class, metadata, inits);
    }

    public QShopInventoryItem(Class<? extends ShopInventoryItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new org.salespointframework.inventory.QMultiInventoryItem(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new clockshop.catalog.QArticle(forProperty("article"), inits.get("article")) : null;
        this.inventoryItemIdentifier = _super.inventoryItemIdentifier;
        this.product = _super.product;
        this.quantity = _super.quantity;
    }

}

