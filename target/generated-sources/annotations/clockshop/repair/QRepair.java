package clockshop.repair;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRepair is a Querydsl query type for Repair
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRepair extends EntityPathBase<Repair> {

    private static final long serialVersionUID = 1360219332L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRepair repair = new QRepair("repair");

    public final org.salespointframework.catalog.QProduct _super;

    //inherited
    public final SetPath<String, StringPath> categories;

    public final NumberPath<Integer> costEstimate = createNumber("costEstimate", Integer.class);

    public final StringPath customerAddress = createString("customerAddress");

    public final StringPath customerForename = createString("customerForename");

    public final StringPath customerName = createString("customerName");

    public final DateTimePath<java.time.LocalDateTime> date = createDateTime("date", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> durationInMinutes = createNumber("durationInMinutes", Long.class);

    public final StringPath email = createString("email");

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final BooleanPath finished = createBoolean("finished");

    public final org.salespointframework.catalog.QProduct_ProductIdentifier id;

    //inherited
    public final EnumPath<org.salespointframework.quantity.Metric> metric;

    //inherited
    public final StringPath name;

    public final org.salespointframework.order.QOrder_OrderIdentifier orderIdentifier;

    //inherited
    public final SimplePath<javax.money.MonetaryAmount> price;

    public final BooleanPath radio = createBoolean("radio");

    public final EnumPath<RepairType> repairType = createEnum("repairType", RepairType.class);

    public final StringPath status = createString("status");

    public final StringPath telephoneNumber = createString("telephoneNumber");

    public QRepair(String variable) {
        this(Repair.class, forVariable(variable), INITS);
    }

    public QRepair(Path<? extends Repair> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRepair(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRepair(PathMetadata metadata, PathInits inits) {
        this(Repair.class, metadata, inits);
    }

    public QRepair(Class<? extends Repair> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new org.salespointframework.catalog.QProduct(type, metadata, inits);
        this.categories = _super.categories;
        this.id = inits.isInitialized("id") ? new org.salespointframework.catalog.QProduct_ProductIdentifier(forProperty("id")) : null;
        this.metric = _super.metric;
        this.name = _super.name;
        this.orderIdentifier = inits.isInitialized("orderIdentifier") ? new org.salespointframework.order.QOrder_OrderIdentifier(forProperty("orderIdentifier")) : null;
        this.price = _super.price;
    }

}

