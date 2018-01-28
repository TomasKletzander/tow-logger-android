package cz.dmn.towlogger.core.io.api.models

import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel(Parcel.Serialization.BEAN)
data class Qualifications @ParcelConstructor constructor(val gld: Boolean, val tow: Boolean)
