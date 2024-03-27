package clockshop.accountancy;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSortoutAccountancyEntry is a Querydsl query type for SortoutAccountancyEntry
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSortoutAccountancyEntry extends EntityPathBase<SortoutAccountancyEntry> {

    private static final long serialVersionUID = 1610241642L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSortoutAccountancyEntry sortoutAccountancyEntry = new QSortoutAccountancyEntry("sortoutAccountancyEntry");

    public final org.salespointframework.accountancy.QAccountancyEntry _super;

    // inherited
    public final org.salespointframework.accountancy.QAccountancyEntry_AccountancyEntryIdentifier accountancyEntryIdentifier;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> date;

    //inherited
    public final StringPath description;

    public final org.salespointframework.order.QOrder_OrderIdentifier orderIdentifier;

    public final org.salespointframework.catalog.QProduct_ProductIdentifier productId;

    public final org.salespointframework.quantity.QQuantity quantity;

    //inherited
    public final SimplePath<javax.money.MonetaryAmount> value;

    public QSortoutAccountancyEntry(String variable) {
        this(SortoutAccountancyEntry.class, forVariable(variable), INITS);
    }

    public QSortoutAccountancyEntry(Path<? extends SortoutAccountancyEntry> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSortoutAccountancyEntry(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSortoutAccountancyEntry(PathMetadata metadata, PathInits inits) {
        this(SortoutAccountancyEntry.class, metadata, inits);
    }

    public QSortoutAccountancyEntry(Class<? extends SortoutAccountancyEntry> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new org.salespointframework.accountancy.QAccountancyEntry(type, metadata, inits);
        this.accountancyEntryIdentifier = _super.accountancyEntryIdentifier;
        this.date = _super.date;
        this.description = _super.description;
        this.orderIdentifier = inits.isInitialized("orderIdentifier") ? new org.salespointframework.order.QOrder_OrderIdentifier(forProperty("orderIdentifier")) : null;
        this.productId = inits.isInitialized("productId") ? new org.salespointframework.catalog.QProduct_ProductIdentifier(forProperty("productId")) : null;
        this.quantity = inits.isInitialized("quantity") ? new org.salespointframework.quantity.QQuantity(forProperty("quantity")) : null;
        this.value = _super.value;
    }

}

