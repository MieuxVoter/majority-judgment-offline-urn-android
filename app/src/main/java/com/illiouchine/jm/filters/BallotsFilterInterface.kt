package com.illiouchine.jm.filters

import com.illiouchine.jm.model.Poll

interface BallotsFilterInterface {

    fun filter(poll: Poll): Poll

}
