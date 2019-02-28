package com.saveethataskdoor.app.food;

import com.saveethataskdoor.app.model.Cart;

public interface OnCartListener {

    public void onCartDelete(Cart cart, int position);

    public void onCartCountUpdate();
}
