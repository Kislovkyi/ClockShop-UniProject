package clockshop.accountancy;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCustomAccountancyEntry is a Querydsl query type for CustomAccountancyEntry
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCustomAccountancyEntry extends EntityPathBase<CustomAccountancyEntry> {

    private static final long serialVersionUID = 1979289531L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCustomAccountancyEntry customAccountancyEntry = new QCustomAccountancyEntry("customAccountancyEntry");

    public final org.salespointframework.accountancy.QAccountancyEntry _super;

    // inherited
    public final org.salespointframework.accountancy.QAccountancyEntry_AccountancyEntryIdentifier accountancyEntryIdentifier;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> date;

    //inherited
    public final StringPath description;

    //inherited
    public final SimplePath<javax.money.MonetaryAmount> value;

    public QCustomAccountancyEntry(String variable) {
        this(CustomAccountancyEntry.class, forVariable(variable), INITS);
    }

    public QCustomAccountancyEntry(Path<? extends CustomAccountancyEntry> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCustomAccountancyEntry(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCustomAccountancyEntry(PathMetadata metadata, PathInits inits) {
        this(CustomAccountancyEntry.class, metadata, inits);
    }

    public QCustomAccountancyEntry(Class<? extends CustomAccountancyEntry> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new org.salespointframework.accountancy.QAccountancyEntry(type, metadata, inits);
        this.accountancyEntryIdentifier = _super.accountancyEntryIdentifier;
        this.date = _super.date;
        this.description = _super.description;
        this.value = _super.value;
    }

}

