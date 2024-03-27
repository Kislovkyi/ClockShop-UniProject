package clockshop.accountancy;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSalaryAccountancyEntry is a Querydsl query type for SalaryAccountancyEntry
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSalaryAccountancyEntry extends EntityPathBase<SalaryAccountancyEntry> {

    private static final long serialVersionUID = 1593572372L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSalaryAccountancyEntry salaryAccountancyEntry = new QSalaryAccountancyEntry("salaryAccountancyEntry");

    public final org.salespointframework.accountancy.QAccountancyEntry _super;

    // inherited
    public final org.salespointframework.accountancy.QAccountancyEntry_AccountancyEntryIdentifier accountancyEntryIdentifier;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> date;

    //inherited
    public final StringPath description;

    public final clockshop.staff.QEmployee_EmployeeIdentifier employeeIdentifier;

    public final org.salespointframework.order.QOrder_OrderIdentifier orderIdentifier;

    //inherited
    public final SimplePath<javax.money.MonetaryAmount> value;

    public QSalaryAccountancyEntry(String variable) {
        this(SalaryAccountancyEntry.class, forVariable(variable), INITS);
    }

    public QSalaryAccountancyEntry(Path<? extends SalaryAccountancyEntry> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSalaryAccountancyEntry(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSalaryAccountancyEntry(PathMetadata metadata, PathInits inits) {
        this(SalaryAccountancyEntry.class, metadata, inits);
    }

    public QSalaryAccountancyEntry(Class<? extends SalaryAccountancyEntry> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new org.salespointframework.accountancy.QAccountancyEntry(type, metadata, inits);
        this.accountancyEntryIdentifier = _super.accountancyEntryIdentifier;
        this.date = _super.date;
        this.description = _super.description;
        this.employeeIdentifier = inits.isInitialized("employeeIdentifier") ? new clockshop.staff.QEmployee_EmployeeIdentifier(forProperty("employeeIdentifier")) : null;
        this.orderIdentifier = inits.isInitialized("orderIdentifier") ? new org.salespointframework.order.QOrder_OrderIdentifier(forProperty("orderIdentifier")) : null;
        this.value = _super.value;
    }

}

