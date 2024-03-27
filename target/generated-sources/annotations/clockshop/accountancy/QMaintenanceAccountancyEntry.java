package clockshop.accountancy;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMaintenanceAccountancyEntry is a Querydsl query type for MaintenanceAccountancyEntry
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMaintenanceAccountancyEntry extends EntityPathBase<MaintenanceAccountancyEntry> {

    private static final long serialVersionUID = -381850995L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMaintenanceAccountancyEntry maintenanceAccountancyEntry = new QMaintenanceAccountancyEntry("maintenanceAccountancyEntry");

    public final org.salespointframework.accountancy.QAccountancyEntry _super;

    // inherited
    public final org.salespointframework.accountancy.QAccountancyEntry_AccountancyEntryIdentifier accountancyEntryIdentifier;

    public final StringPath address = createString("address");

    public final StringPath company = createString("company");

    public final StringPath contactPerson = createString("contactPerson");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> date;

    //inherited
    public final StringPath description;

    public final org.salespointframework.order.QOrder_OrderIdentifier orderIdentifier;

    //inherited
    public final SimplePath<javax.money.MonetaryAmount> value;

    public QMaintenanceAccountancyEntry(String variable) {
        this(MaintenanceAccountancyEntry.class, forVariable(variable), INITS);
    }

    public QMaintenanceAccountancyEntry(Path<? extends MaintenanceAccountancyEntry> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMaintenanceAccountancyEntry(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMaintenanceAccountancyEntry(PathMetadata metadata, PathInits inits) {
        this(MaintenanceAccountancyEntry.class, metadata, inits);
    }

    public QMaintenanceAccountancyEntry(Class<? extends MaintenanceAccountancyEntry> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new org.salespointframework.accountancy.QAccountancyEntry(type, metadata, inits);
        this.accountancyEntryIdentifier = _super.accountancyEntryIdentifier;
        this.date = _super.date;
        this.description = _super.description;
        this.orderIdentifier = inits.isInitialized("orderIdentifier") ? new org.salespointframework.order.QOrder_OrderIdentifier(forProperty("orderIdentifier")) : null;
        this.value = _super.value;
    }

}

