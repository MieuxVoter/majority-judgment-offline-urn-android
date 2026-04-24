package com.illiouchine.jm.filters

import com.illiouchine.jm.model.Poll

class NoBallotFilter: BallotsFilterInterface {

    override fun filter(poll: Poll): Poll {
        return poll
    }

}
