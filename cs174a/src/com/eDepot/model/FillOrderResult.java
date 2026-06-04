package com.eDepot.model;

import java.util.ArrayList;
import java.util.List;

public class FillOrderResult {
    public List<String> fulfilled = new ArrayList<>();
    public List<String> skipped = new ArrayList<>();
    public List<String> replenishmentOrders = new ArrayList<>();
}
