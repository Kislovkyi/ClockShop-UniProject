package clockshop.maintenance;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMaintenanceContract is a Querydsl query type for MaintenanceContract
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMaintenanceContract extends EntityPathBase<MaintenanceContract> {

    private static final long serialVersionUID = -1545140256L;

    public static final QMaintenanceContract maintenanceContract = new QMaintenanceContract("maintenanceContract");

    public final StringPath address = createString("address");

    public final NumberPath<Integer> buildingQuantity = createNumber("buildingQuantity", Integer.class);

    public final StringPath company = createString("company");

    public final StringPath contactPerson = createString("contactPerson");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ComparablePath<org.javamoney.moneta.Money> price = createComparable("price", org.javamoney.moneta.Money.class);

    public final NumberPath<Integer> towerQuantity = createNumber("towerQuantity", Integer.class);

    public QMaintenanceContract(String variable) {
        super(MaintenanceContract.class, forVariable(variable));
    }

    public QMaintenanceContract(Path<? extends MaintenanceContract> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMaintenanceContract(PathMetadata metadata) {
        super(MaintenanceContract.class, metadata);
    }

}

