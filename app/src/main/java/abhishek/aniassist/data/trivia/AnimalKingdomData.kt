package abhishek.aniassist.data.trivia

/**
 * Animal Kingdom dataset — ported from the trivia website's constants.js.
 * Images are remote URLs loaded via Coil (keeps APK small; dead links
 * fall back to the placeholder drawable).
 */

enum class Habitat(val label: String) { AIR("Air"), LAND("Land"), WATER("Water") }

data class TriviaVariety(
    val name: String,
    val info: String,        // e.g. "Origin: UK • Size: Large" or scientific name + location
    val temperament: String = "",
    val description: String
)

data class TriviaAnimal(
    val name: String,
    val imageUrl: String,
    val scientificName: String,
    val habitat: String,
    val diet: String,
    val lifespan: String,
    val description: String,
    val varieties: List<TriviaVariety> = emptyList()
)

object AnimalKingdomData {

    val air = listOf(
        TriviaAnimal(
            "Parrots",
            "https://www.treehugger.com/thmb/F5G8zaALRp7rvgWqDVT13t9pKY8=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/close-up-of-scarlet-macaw-flying-in-mid-air-634869043-f360b379b8c44a28a052b41d99adc2a7.jpg",
            "Psittaciformes", "Tropical and subtropical regions",
            "Fruits, seeds, nuts, and vegetation", "20-80 years",
            "Highly intelligent birds known for their colorful plumage and ability to mimic human speech."
        ),
        TriviaAnimal(
            "Canaries",
            "https://cdn.britannica.com/33/226533-050-404C15AF/Canary-on-pear-branch.jpg",
            "Serinus canaria", "Islands in the Atlantic Ocean",
            "Seeds, fruits, vegetables", "5-10 years",
            "Small songbirds prized for their melodious singing and vibrant yellow plumage."
        ),
        TriviaAnimal(
            "Budgerigars",
            "https://cdn.download.ams.birds.cornell.edu/api/v1/asset/123378071/900",
            "Melopsittacus undulatus", "Australia",
            "Seeds, vegetables, fruits", "5-10 years",
            "Commonly known as budgies, these small parrots are popular as pets due to their playful nature and ability to mimic sounds."
        ),
        TriviaAnimal(
            "Pigeons",
            "https://cdn.britannica.com/53/117053-004-9BDDBB1A/Rock.jpg",
            "Columba livia", "Urban areas, cliffs, buildings",
            "Seeds, grains, fruits", "3-5 years",
            "Domesticated pigeons are commonly kept as pets or for racing and messenger purposes."
        ),
        TriviaAnimal(
            "Eagles",
            "https://cdn.britannica.com/92/152292-050-EAF28A45/Bald-eagle.jpg",
            "Accipitridae", "Various habitats worldwide",
            "Small mammals, birds, fish", "20-30 years",
            "Large birds of prey known for their powerful beaks and keen eyesight, often symbolizing strength and freedom."
        ),
        TriviaAnimal(
            "Hawks",
            "https://cdn.britannica.com/86/117086-050-81460DD9/Red-tailed-hawk.jpg",
            "Accipitridae", "Various habitats worldwide",
            "Small mammals, birds, reptiles", "10-20 years",
            "Birds of prey with sharp talons and excellent hunting skills, found in diverse ecosystems."
        ),
        TriviaAnimal(
            "Owls",
            "https://www.allaboutbirds.org/guide/assets/photo/297366501-480px.jpg",
            "Strigiformes", "Forests, deserts, tundra",
            "Small mammals, birds, insects", "5-20 years",
            "Nocturnal birds of prey known for their silent flight and distinctive hooting calls."
        ),
        TriviaAnimal(
            "Songbirds",
            "https://cff2.earth.com/uploads/2022/09/15085629/Bird-colors-960x640.jpg",
            "Various families", "Varies by species",
            "Seeds, insects, fruits", "2-10 years",
            "Small to medium-sized birds known for their melodious songs and diverse plumage."
        )
    )

    val land = listOf(
        TriviaAnimal(
            "Dogs",
            "https://thumbor.forbes.com/thumbor/fit-in/900x510/https://www.forbes.com/advisor/wp-content/uploads/2023/07/top-20-small-dog-breeds.jpeg.jpg",
            "Canis lupus familiaris", "Domesticated, various environments",
            "Commercial dog food, meat, vegetables", "8-15 years (depending on breed)",
            "Domesticated canids known for their loyalty, diverse breeds, and roles as companions, working dogs, and service animals.",
            varieties = listOf(
                TriviaVariety("Labrador Retriever", "Origin: United Kingdom • Medium to large • 10-12 yrs",
                    "Friendly, outgoing, gentle", "Intelligent, versatile, and friendly breed known for their obedience and athleticism."),
                TriviaVariety("German Shepherd", "Origin: Germany • Large • 9-13 yrs",
                    "Loyal, courageous, confident", "Versatile and highly trainable breed used in police work, search and rescue, and as family companions."),
                TriviaVariety("Golden Retriever", "Origin: United Kingdom • Medium to large • 10-12 yrs",
                    "Friendly, intelligent, devoted", "Friendly and tolerant breed known for their gentle disposition, making them excellent family pets."),
                TriviaVariety("Bulldog", "Origin: England • Medium • 8-10 yrs",
                    "Docile, willful, friendly", "Sturdy and muscular breed with a distinctive pushed-in nose, known for their calm and courageous demeanor."),
                TriviaVariety("Poodle", "Origin: France • Medium • 10-18 yrs",
                    "Active, alert, intelligent", "Highly intelligent and active breed, known for their hypoallergenic coat and variety of sizes."),
                TriviaVariety("Beagle", "Origin: United Kingdom • Small to medium • 10-15 yrs",
                    "Friendly, curious, merry", "Energetic and friendly breed with a keen sense of smell, often used for hunting and as family companions."),
                TriviaVariety("Siberian Husky", "Origin: Siberia • Medium to large • 12-14 yrs",
                    "Outgoing, gentle, alert", "Energetic and resilient breed with a thick coat, originally bred as sled dogs in cold climates.")
            )
        ),
        TriviaAnimal(
            "Cats",
            "https://images.pexels.com/photos/45201/kitty-cat-kitten-pet-45201.jpeg",
            "Felis catus", "Domesticated, human homes",
            "Commercial cat food, meat, grains", "12-20 years (depending on breed)",
            "Domesticated felines known for their independence, agility, and hunting abilities, often kept as indoor or outdoor pets.",
            varieties = listOf(
                TriviaVariety("Siamese", "Origin: Thailand • Medium • 12-15 yrs",
                    "Affectionate, social, vocal", "Elegant and vocal breed known for their striking blue eyes, sleek coat, and affectionate nature."),
                TriviaVariety("Maine Coon", "Origin: United States • Large • 12-15 yrs",
                    "Gentle, friendly, playful", "One of the largest domesticated breeds, known for their friendly demeanor, tufted ears, and long bushy tail."),
                TriviaVariety("Persian", "Origin: Iran (Persia) • Medium to large • 10-15 yrs",
                    "Sweet, calm, affectionate", "Long-haired breed with a sweet and calm temperament, known for their luxurious coat and expressive eyes."),
                TriviaVariety("Bengal", "Origin: United States • Medium • 10-15 yrs",
                    "Active, playful, intelligent", "Exotic-looking breed with a wild appearance, known for their playful and energetic nature."),
                TriviaVariety("Ragdoll", "Origin: United States • Medium to large • 12-17 yrs",
                    "Affectionate, docile, gentle", "Laid-back and affectionate breed known for their relaxed disposition and floppy ragdoll-like behavior."),
                TriviaVariety("Sphynx", "Origin: Canada • Medium • 8-14 yrs",
                    "Curious, affectionate, energetic", "Hairless breed with wrinkled skin, known for their affectionate nature, high energy levels, and playful antics."),
                TriviaVariety("Scottish Fold", "Origin: Scotland • Small to medium • 11-14 yrs",
                    "Sweet, gentle, curious", "Distinctive breed with folded ears, known for their sweet and gentle nature, making them excellent companions.")
            )
        ),
        TriviaAnimal(
            "Rabbits",
            "https://www.taiyogroup.in/wp-content/uploads/2022/04/Rabbits.jpg",
            "Oryctolagus cuniculus", "Varies by species, burrows, grasslands",
            "Hay, vegetables, pellets", "5-12 years (depending on breed)",
            "Small mammals known for their long ears, hopping locomotion, and herbivorous diet, popular as pets and in rabbit husbandry.",
            varieties = listOf(
                TriviaVariety("Dutch Rabbit", "Origin: Netherlands • Small to medium • 5-8 yrs",
                    "Friendly, playful, curious", "Distinctive breed with a white body and colored markings around the eyes, ears, and tail."),
                TriviaVariety("Lionhead Rabbit", "Origin: Belgium • Small to medium • 7-10 yrs",
                    "Gentle, social, curious", "Small breed with a distinctive mane of longer fur around their head, known for their gentle temperament."),
                TriviaVariety("Mini Rex Rabbit", "Origin: United States • Small • 5-7 yrs",
                    "Curious, affectionate, intelligent", "Small breed with a short, velvety fur coat, known for their curious and intelligent nature."),
                TriviaVariety("Holland Lop Rabbit", "Origin: Netherlands • Small to medium • 7-12 yrs",
                    "Gentle, social, friendly", "Compact breed with lopped ears, known for their gentle and friendly demeanor."),
                TriviaVariety("Flemish Giant Rabbit", "Origin: Belgium • Giant • 5-10 yrs",
                    "Gentle, docile, friendly", "One of the largest domesticated rabbit breeds, known for their gentle and friendly nature.")
            )
        ),
        TriviaAnimal(
            "Guinea Pigs",
            "https://cdn.mos.cms.futurecdn.net/gJJFamQca86CibEeDmegk-1200-80.jpg",
            "Cavia porcellus", "Domesticated, indoor enclosures",
            "Hay, vegetables, pellets", "4-8 years",
            "Small rodents often kept as pets for their docile nature, sociability, and vocalizations.",
            varieties = listOf(
                TriviaVariety("Abyssinian", "Origin: South America • Small • 4-8 yrs",
                    "Curious, social, active", "Known for their unique coat with rosettes, these are curious and social pets that enjoy interaction."),
                TriviaVariety("American", "Origin: South America • Small • 4-7 yrs",
                    "Friendly, docile, easygoing", "Popular breed with smooth, short hair and a friendly disposition, great for families."),
                TriviaVariety("Peruvian", "Origin: South America • Small • 5-7 yrs",
                    "Gentle, affectionate, high maintenance", "Known for their long, flowing hair that requires regular grooming."),
                TriviaVariety("Teddy", "Origin: South America • Small • 5-7 yrs",
                    "Calm, friendly, cuddly", "Characterized by their dense, soft coat that resembles a teddy bear; calm and cuddly."),
                TriviaVariety("Silkie", "Origin: South America • Small • 5-8 yrs",
                    "Sweet, affectionate, requires grooming", "Known for their long, silky hair that needs regular grooming; sweet and affectionate.")
            )
        ),
        TriviaAnimal(
            "Lions",
            "https://c02.purpledshub.com/uploads/sites/62/2019/10/Federico_Veronesi_Lions-cover-image-e359a4e.jpg?webp=1&w=1200",
            "Panthera leo", "Savannas, grasslands, woodlands",
            "Large mammals, ungulates", "10-15 years (in the wild)",
            "Large carnivorous felids known for their social structure, hunting prowess, and iconic mane in males.",
            varieties = listOf(
                TriviaVariety("African Lion", "Panthera leo • Sub-Saharan Africa • 10-14 yrs",
                    "Social, live in prides", "Apex predators known for their majestic appearance, social structure, and hunting prowess — the king of the jungle."),
                TriviaVariety("Asian Lion", "Panthera leo persica • Gir Forest, India • 10-14 yrs",
                    "Social, live in prides", "A subspecies found only in a small region of India, adapted to dry deciduous forests.")
            )
        ),
        TriviaAnimal(
            "Tigers",
            "https://media.4-paws.org/5/4/4/c/544c2b2fd37541596134734c42bf77186f0df0ae/VIER%20PFOTEN_2017-10-20_164-3854x2667-1920x1329.jpg",
            "Panthera tigris", "Forests, grasslands, mangrove swamps",
            "Large mammals, ungulates, prey animals", "10-15 years (in the wild)",
            "Iconic large cats with distinctive stripes, known for their strength, agility, and solitary nature.",
            varieties = listOf(
                TriviaVariety("Bengal Tiger", "Panthera tigris tigris • India, Bangladesh, Nepal • 8-10 yrs",
                    "Solitary hunter", "The most numerous tiger subspecies, known for their distinctive orange coat with black stripes."),
                TriviaVariety("Siberian Tiger", "Panthera tigris altaica • Russia, China • 10-15 yrs",
                    "Solitary hunter", "Also known as Amur tigers — the largest of all tiger species, adapted to cold climates.")
            )
        ),
        TriviaAnimal(
            "Leopards",
            "https://www.krugerpark.co.za/images/leopard-kruger-rh-786x500.jpg",
            "Panthera pardus", "Forests, grasslands, mountains",
            "Small to large mammals, birds", "12-17 years (in the wild)",
            "Agile and elusive big cats with a wide habitat range, known for their spotted coat patterns and nocturnal habits.",
            varieties = listOf(
                TriviaVariety("African Leopard", "Panthera pardus pardus • Sub-Saharan Africa • 12-17 yrs",
                    "Solitary and nocturnal", "Known for their distinctive golden-yellow coat with black spots called rosettes."),
                TriviaVariety("Snow Leopard", "Panthera uncia • Central & South Asia • 10-12 yrs",
                    "Solitary and crepuscular", "Adapted to cold, harsh mountain environments with a thick fur coat.")
            )
        ),
        TriviaAnimal(
            "Wolves",
            "https://cdn.britannica.com/49/144449-050-6F5870A7/Mexican-gray-wolf.jpg",
            "Canis lupus", "Forests, tundra, grasslands",
            "Large mammals, ungulates", "6-8 years (in the wild)",
            "Social canids with complex pack structures, known for their hunting prowess, howling vocalizations, and symbolic significance.",
            varieties = listOf(
                TriviaVariety("Gray Wolf", "Canis lupus • North America, Eurasia • 6-13 yrs",
                    "Social, territorial, pack animals", "Known for their distinctive howling, pack behavior, and territorial nature."),
                TriviaVariety("Arctic Wolf", "Canis lupus arctos • Arctic regions • 7-10 yrs",
                    "Social, pack animals", "Has a thick white fur coat that helps them blend in with snowy surroundings.")
            )
        )
    )

    val water = listOf(
        TriviaAnimal(
            "Goldfish",
            "https://cafishvet.com/wp-content/uploads/2021/05/Gold-Fish-.jpeg",
            "Carassius auratus", "Aquariums, ponds, lakes",
            "Fish flakes, vegetables, algae", "10-15 years",
            "Colorful freshwater fish popular in aquariums, known for their adaptability and ease of care.",
            varieties = listOf(
                TriviaVariety("Common Goldfish", "Carassius auratus • East Asia • 10-15 yrs",
                    "Schooling, social, peaceful", "Popular freshwater aquarium fish known for their bright colors and long fins."),
                TriviaVariety("Fantail Goldfish", "Carassius auratus • East Asia • 10-15 yrs",
                    "Schooling, social, peaceful", "Characterized by their double tail fins that spread out like a fan.")
            )
        ),
        TriviaAnimal(
            "Tropical Fish",
            "https://img.freepik.com/premium-photo/tropical-fish_883586-22254.jpg",
            "Various families", "Aquariums, coral reefs",
            "Fish pellets, live food, plants", "1-10 years (depending on species)",
            "Colorful and diverse fish species native to tropical regions, often kept in aquariums for their vibrant colors and patterns.",
            varieties = listOf(
                TriviaVariety("Guppy", "Poecilia reticulata • South America • 1-3 yrs",
                    "Schooling, active, social", "Popular freshwater fish known for their vibrant colors, small size, and ease of care."),
                TriviaVariety("Betta Fish", "Betta splendens • Southeast Asia • 2-3 yrs",
                    "Solitary, territorial", "Also known as Siamese fighting fish — vibrant colors, long fins, territorial behavior.")
            )
        ),
        TriviaAnimal(
            "Koi Fish",
            "https://t4.ftcdn.net/jpg/05/75/48/57/360_F_575485756_WSQ6ZzqMhD0JnPcEupxyKikKKCE5p5jo.jpg",
            "Cyprinus rubrofuscus", "Ponds, water gardens",
            "Pellets, insects, algae", "20-30 years",
            "Large ornamental carp known for their beautiful colors and patterns, often kept in decorative ponds.",
            varieties = listOf(
                TriviaVariety("Kohaku", "Cyprinus carpio • Japan • 20-30 yrs",
                    "Schooling, calm, peaceful", "One of the most popular koi varieties, known for their white body and red markings."),
                TriviaVariety("Sanke", "Cyprinus carpio • Japan • 20-30 yrs",
                    "Schooling, calm, peaceful", "White body with red and black markings — a popular choice among koi enthusiasts.")
            )
        ),
        TriviaAnimal(
            "Axolotls",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTROJKPYmqxp1SUjz3yoTM0Dc19c37zuPi9pQ&s",
            "Ambystoma mexicanum", "Aquariums, freshwater habitats",
            "Worms, insects, small fish", "10-15 years",
            "Aquatic salamanders known for their regenerative abilities, unique appearance, and neotenic features.",
            varieties = listOf(
                TriviaVariety("Wild-type", "Ambystoma mexicanum • Mexico • 10-15 yrs",
                    "Solitary, nocturnal", "Unique aquatic creatures known for their regenerative abilities and neotenic features."),
                TriviaVariety("Leucistic", "Ambystoma mexicanum • Mexico • 10-15 yrs",
                    "Solitary, nocturnal", "A color morph with a pale pink or white body, lacking pigment in their skin.")
            )
        ),
        TriviaAnimal(
            "Dolphins",
            "https://cdn.vallarta-adventures.com/sites/default/files/2021-08/dolphin-facts.jpg",
            "Delphinidae", "Oceans, seas, coastal areas",
            "Fish, squid, crustaceans", "20-50 years",
            "Highly intelligent marine mammals known for their playful behavior, communication skills, and acrobatic abilities.",
            varieties = listOf(
                TriviaVariety("Bottlenose Dolphin", "Tursiops truncatus • Global oceans • 40-60 yrs",
                    "Highly social, live in pods", "Well-known for their intelligence, playfulness, and distinctive bottle-shaped snout."),
                TriviaVariety("Spinner Dolphin", "Stenella longirostris • Tropical waters • 20-25 yrs",
                    "Social, acrobatic", "Known for their remarkable spinning jumps and social behaviors within large groups.")
            )
        ),
        TriviaAnimal(
            "Whales",
            "https://c02.purpledshub.com/uploads/sites/62/2019/10/GettyImages-1164887104_Craig-Lambert-2faf563.jpg?w=1029&webp=1",
            "Various families", "Oceans, seas",
            "Krill, fish, plankton", "50-100 years",
            "Giant marine mammals ranging from the massive blue whale to the agile killer whale, known for their migrations and vocalizations.",
            varieties = listOf(
                TriviaVariety("Blue Whale", "Balaenoptera musculus • All oceans • 70-90 yrs",
                    "Migratory, low-frequency calls", "The largest animals on Earth, known for their immense size and deep, resonant calls."),
                TriviaVariety("Killer Whale (Orca)", "Orcinus orca • All oceans • up to 80 yrs",
                    "Highly social, apex predators", "Apex predators known for their intelligence, hunting skills, and complex social structures.")
            )
        ),
        TriviaAnimal(
            "Sharks",
            "https://cdn.britannica.com/79/65379-050-5CF52BAC/Shortfin-mako-shark-seas.jpg",
            "Selachimorpha", "Oceans, seas",
            "Fish, seals, marine mammals", "20-70 years (depending on species)",
            "Apex predators of the marine ecosystem with diverse species ranging from the massive great white shark to the elusive hammerhead.",
            varieties = listOf(
                TriviaVariety("Great White Shark", "Carcharodon carcharias • Coastal waters • up to 70 yrs",
                    "Apex predator, migratory", "Powerful apex predators known for their size, speed, and distinctive white belly."),
                TriviaVariety("Hammerhead Shark", "Sphyrnidae family • Tropical & temperate waters • 25-35 yrs",
                    "Distinctive hammer-shaped head", "Known for their unique head structure, often found near coral reefs.")
            )
        ),
        TriviaAnimal(
            "Seals",
            "https://cff2.earth.com/uploads/2023/12/15165936/Arctic-bearded-seal_1medium.jpg",
            "Pinnipeds", "Coastlines, islands",
            "Fish, squid, crustaceans", "20-30 years",
            "Marine mammals with streamlined bodies and flippers, known for their agility in water and playful behavior.",
            varieties = listOf(
                TriviaVariety("Harbor Seal", "Phoca vitulina • Temperate & Arctic coasts • 20-35 yrs",
                    "Social, playful", "Common along temperate and Arctic coastlines, known for their playful nature and agility."),
                TriviaVariety("Elephant Seal", "Mirounga • Subpolar & temperate coasts • 20-25 yrs",
                    "Migratory, males have large proboscis", "Named for the large proboscis of adult males; excellent swimmers and divers.")
            )
        )
    )

    fun forHabitat(habitat: Habitat) = when (habitat) {
        Habitat.AIR -> air
        Habitat.LAND -> land
        Habitat.WATER -> water
    }
}
