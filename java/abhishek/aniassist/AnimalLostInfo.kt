package abhishek.aniassist


data class AnimalLostInfo (
    var category: String? = null,
    var animName:String? = null,
    var ownerName:String? = null,
    var ownerNumber:String? = null,
    var description:String? = null,
    var lastSighted:String? = null,
    var animalId:String? = null,
    var dateTime:String? = null,
    var uri:String? = null,
    var stillLost:Boolean = true
)