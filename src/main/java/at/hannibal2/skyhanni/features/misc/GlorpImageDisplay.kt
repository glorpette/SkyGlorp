package at.hannibal2.skyhanni.features.misc

import at.hannibal2.skyhanni.SkyHanniMod
import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.config.commands.CommandCategory
import at.hannibal2.skyhanni.config.commands.CommandRegistrationEvent
import at.hannibal2.skyhanni.events.GuiRenderEvent
import at.hannibal2.skyhanni.events.chat.SkyHanniChatEvent
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.SimpleTimeMark
import at.hannibal2.skyhanni.utils.SoundUtils
import at.hannibal2.skyhanni.utils.SoundUtils.playSound
import at.hannibal2.skyhanni.utils.compat.GuiScreenUtils
import at.hannibal2.skyhanni.utils.compat.RenderCompat
import at.hannibal2.skyhanni.utils.compat.createResourceLocation
import kotlin.time.Duration.Companion.seconds

@SkyHanniModule
object GlorpImageDisplay {

    private val textureLocation = createResourceLocation("skyhanni", "glorp.png")
    private var showImageUntil = SimpleTimeMark.farPast()

    // Cached image dimensions
    private const val imageDisplaySize = 64
    private const val imageTextureSize = 64

    @HandleEvent
    fun onChat(event: SkyHanniChatEvent) {
        if (!SkyHanniMod.feature.misc.glorp) return

        val message = event.message

        // Only process player messages (must have ": " separator)
        val colonIndex = message.indexOf(": ")
        if (colonIndex == -1) return

        // Extract actual chat message content (after ": ")
        val actualMessage = message.substring(colonIndex + 2)

        // Check if message content contains "glorp" (case insensitive)
        if (actualMessage.contains("glorp", ignoreCase = true)) {
            SoundUtils.glorpSound.playSound()
            showImageUntil = SimpleTimeMark.now() + 2.seconds
        }
    }

    @HandleEvent
    fun onRenderOverlay(event: GuiRenderEvent.GuiOverlayRenderEvent) {
        if (!showImageUntil.isInFuture()) {
            return
        }

        // Get screen dimensions
        val screenWidth = GuiScreenUtils.scaledWindowWidth
        val screenHeight = GuiScreenUtils.scaledWindowHeight

        // Calculate centered position horizontally, 15% from top vertically
        val x = (screenWidth - imageDisplaySize) / 2
        val y = (screenHeight * 0.15).toInt()

        event.context.blit(
            RenderCompat.getMinecraftGuiTextured(),
            textureLocation,
            x,
            y,
            0f,
            0f,
            imageDisplaySize,
            imageDisplaySize,
            imageTextureSize,
            imageTextureSize,
        )
    }

    @HandleEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.registerBrigadier("shshowglorp") {
            description = "Display the Glorp image for 5 seconds for testing"
            category = CommandCategory.DEVELOPER_TEST
            simpleCallback {
                showImageUntil = SimpleTimeMark.now() + 5.seconds
            }
        }
    }
}
