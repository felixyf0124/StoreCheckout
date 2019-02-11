package point.of.sale;

import java.util.ArrayList;

import com.google.inject.Inject;

public class Sale {
	
	@Inject
	HashStorage hashLookup;
	@Inject
	Display display;
	@Inject
	Interac interac;
	
	ArrayList<String> items = new ArrayList<>();
	
	//for testing purposes
	@Inject
	public Sale(Display display, HashStorage hashStorage, Interac interac) {
		this.display = display;
		this.hashLookup = hashStorage;
		this.interac = interac;
		
		display.showLine(StoreInfo.getInstance().getName());
		
	}
	
	//original legacy
	public Sale() {
		
		this(new ArtR56Display(), new HashStorage(), new Interac(12));
		
		//Storage, add the items in the store
		hashLookup = new HashStorage();
		hashLookup.put("1A", "Milk, 3.99");
		hashLookup.put("2A", "Bread, 4.99");
		
	}
	
	public void completePurchase() {
		interac.pay(items);
	}
	
	public void scan(String barcode) {
				
		//lookup barcode in storage and get item
		String item = hashLookup.barcode(barcode);
		
		//display the barcode
		display.showLine(barcode);
		
		//display the item
		display.showLine(item);
		
		//Store it for payment 
		items.add(item);
		
	}

	public void testingOnlySupercedeInterac(Interac interac) {
		this.interac = interac;
		
	}

}
