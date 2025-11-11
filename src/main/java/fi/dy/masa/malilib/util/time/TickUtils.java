package fi.dy.masa.malilib.util.time;

import org.jetbrains.annotations.ApiStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.mixin.server.IMixinServerTickManager;
import fi.dy.masa.malilib.util.MathUtils;

/**
 * Tick Rate / Measurement utility that tracks and calculates the server's Tick Rate over time.
 */
public class TickUtils
{
    private static final Data INSTANCE = new Data();
    public static Data getInstance() { return INSTANCE; }

    /**
     * Returns the actual Vanilla Tick Rate.
     * @return ()
     */
    public static float getTickRate()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.isIntegratedServerRunning() && mc.getServer() != null)
        {
            return mc.getServer().getTickManager().getTickRate();
        }
        else if (mc.world != null)
        {
            return mc.world.getTickManager().getTickRate();
        }

        return -1F;
    }

    /**
     * Get the Vanilla MSPT measurement.
     * @return ()
     */
    public static float getMillisPerTick()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.isIntegratedServerRunning() && mc.getServer() != null)
        {
            return mc.getServer().getTickManager().getMillisPerTick();
        }
        else if (mc.world != null)
        {
            return mc.world.getTickManager().getMillisPerTick();
        }

        return -1F;
    }

    /**
     * Return the Vanilla Sprint Ticks, if available.
     * @return ()
     */
    public static long getSprintTicks()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.isIntegratedServerRunning() && mc.getServer() != null)
        {
            return ((IMixinServerTickManager) mc.getServer().getTickManager()).malilib_getSprintTicks();
        }
        else if (getInstance().hasServuxData())
        {
            return getInstance().getSprintTicks();
        }

        return -1L;
    }

    /**
     * Return whether Vanilla is currently stepping ticks.
     * @return ()
     */
    public static boolean isStepping()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.isIntegratedServerRunning() && mc.getServer() != null)
        {
            return mc.getServer().getTickManager().isStepping();
        }
        else if (getInstance().hasServuxData())
        {
            return getInstance().isStepping();
        }
        else if (mc.world != null)
        {
            return mc.world.getTickManager().isStepping();
        }

        return false;
    }

    /**
     * Return whether the game is Frozen.
     * @return ()
     */
    public static boolean isFrozen()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.isIntegratedServerRunning() && mc.getServer() != null)
        {
            return mc.getServer().getTickManager().isFrozen();
        }
        else if (getInstance().hasServuxData())
        {
            return getInstance().isFrozen();
        }
        else if (mc.world != null)
        {
            return mc.world.getTickManager().isFrozen();
        }

        return false;
    }

    /**
     * Return whether the game is Sprinting / Tick Warping.
     * @return ()
     */
    public static boolean isSprinting()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.isIntegratedServerRunning() && mc.getServer() != null)
        {
            return mc.getServer().getTickManager().isSprinting();
        }
        else if (getInstance().hasServuxData())
        {
            return getInstance().isSprinting();
        }
        else if (mc.world != null)
        {
            Data timeData = getInstance();

            // MSPT drops when sprinting due to math also.
            return (timeData.hasTimeSynced() && (timeData.getActualTPS() / 3 > timeData.getTickRate()));
        }

        return false;
    }

    /**
     * Return whether the Tick Data is estimated or actual.
     * @return ()
     */
    public static boolean isEstimated()
    {
        return getInstance().hasTimeSynced();
    }

    /**
     * Return whether the Tick Data is valid.
     * @return ()
     */
    public static boolean isValid()
    {
        return getInstance().isValid();
    }

    /**
     * Return whether the Tick Data has direct server data.
     * @return ()
     */
    public static boolean hasDirectData()
    {
        return getInstance().hasDirectData();
    }

    /**
     * Return whether the Tick Data has Servux server data.
     * @return ()
     */
    public static boolean hasServuxData()
    {
        return getInstance().hasServuxData();
    }

    /**
     * Return the measured / calculated MSPT
     * @return ()
     */
    public static double getMeasuredMSPT()
    {
        Data timeData = getInstance();

        if (timeData.isValid())
        {
            return timeData.getMeasuredMSPT();
        }

        return 0.0D;
    }

    /**
     * Return the measured / calculated TPS
     * @return ()
     */
    public static double getMeasuredTPS()
    {
        Data timeData = getInstance();

        if (timeData.isValid())
        {
            return timeData.getMeasuredTPS();
        }

        return 0.0D;
    }

    /**
     * Return the direct remote server MSPT
     * @return ()
     */
    public static double getDirectMSPT()
    {
        Data timeData = getInstance();

        if (timeData.hasDirectData())
        {
            return timeData.getDirectMSPT();
        }

        return 0.0D;
    }

    /**
     * Return the direct remote server TPS
     * @return ()
     */
    public static double getDirectTPS()
    {
        Data timeData = getInstance();

        if (timeData.hasDirectData())
        {
            return timeData.getDirectTPS();
        }

        return 0.0D;
    }

    /**
     * Return the actual (Non-tick limited) TPS based on the Tick Rate of the server.
     * @return ()
     */
    public static double getActualTPS()
    {
        Data timeData = getInstance();

        if (timeData.isValid())
        {
            return timeData.getActualTPS();
        }

        return 0.0D;
    }

    /**
     * These are not meant to be used outside of DEBUG MODE, to save on unused CPU math cycles.
      * @return ()
     */
    @ApiStatus.Experimental
    public static double getAvgMSPT()
    {
        Data timeData = getInstance();

        if (timeData.isValid())
        {
            return timeData.getAverageMSPT();
        }

        return 0.0D;
    }

    /**
     * These are not meant to be used outside of DEBUG MODE, to save on unused CPU math cycles.
     * @return ()
     */
    @ApiStatus.Experimental
    public static double getAvgTPS()
    {
        Data timeData = getInstance();

        if (timeData.isValid())
        {
            return timeData.getAverageTPS();
        }

        return 0.0D;
    }

    /**
     * Internal Data class to store and manage the Tick Information.
     */
    public static class Data
    {
        private double tickRate = TickUtils.getTickRate();
        private double measuredTPS = -1.0D;
        private double measuredMSPT = -1.0D;
        private double directTPS = -1.0D;
        private double directMSPT = -1.0D;
        private double actualTPS = -1.0D;
        private long lastDirectTick = -1L;
        private long lastNanoTick = -1L;
        private long lastNanoTime = -1L;
        private final int MAX_HISTORY = 30;
        private final double[] prevMeasuredMSPT = new double[MAX_HISTORY];
        private final double[] prevMeasuredTPS  = new double[MAX_HISTORY];
        private int lastMeasurementTick = 0;
        private double avgMeasuredMSPT = -1.0D;
        private double avgMeasuredTPS = -1.0D;
        private boolean isValid = false;
        private boolean hasTimeSynced = false;
        private boolean useDirectServerData = false;
        private boolean hasServuxData = false;
        private long sprintTicks = -1L;
        private boolean isFrozen;
        private boolean isSprinting;
        private boolean isStepping;

        private Data() {}

        public void updateTickRate(float tickRate)
        {
            this.tickRate = tickRate;
        }

        /**
         * Return whether this data is supposed to be tracking data directly from Carpet or Servux
         * @return ()
         */
        public boolean isUsingDirectServerData() { return this.useDirectServerData; }

        /**
         * Meant to enable the Carpet / Servux server data mode.
         * @param toggle ()
         */
        public void toggleUseDirectServerData(boolean toggle)
        {
            this.useDirectServerData = toggle;
        }

        @ApiStatus.Internal
        public void updateNanoTick(long timeUpdate)
        {
            if (!MinecraftClient.getInstance().isIntegratedServerRunning())
            {
                final long currentTime = System.nanoTime();

                if (this.hasTimeSynced)
                {
                    final long elapsed = timeUpdate - this.lastNanoTick;

                    if (elapsed > 0)
                    {
                        // Check if Remote Server Direct Data is stale, and
                        // disable (Such as if you disable the TPS logger in Carpet),
                        // give it 3000ms (3s) before shutting it off.
                        if (this.useDirectServerData && this.lastDirectTick > -1L &&
                            (currentTime - this.lastDirectTick) > 3000000000L)
                        {
                            this.toggleUseDirectServerData(false);
                            this.directTPS = -1.0D;
                            this.directMSPT = -1.0D;
                            this.lastDirectTick = -1L;
                            this.sprintTicks = -1L;
                            this.isSprinting = false;
                            this.isFrozen = false;
                            this.isStepping = false;
                            this.hasServuxData = false;
                        }

                        this.measuredMSPT = ((double) (currentTime - this.lastNanoTime) / (double) elapsed) / 1000000D;
                        this.measuredTPS = this.measuredMSPT <= 50 ? this.tickRate : (1000D / this.measuredMSPT);
                        this.actualTPS = (1000D / this.measuredMSPT);
                        if (MaLiLibReference.DEBUG_MODE)
                        {
                            this.calculateAverages();
                        }
                        this.isValid = true;
                    }
                }

                this.lastNanoTick = timeUpdate;
                this.lastNanoTime = currentTime;
                this.hasTimeSynced = true;
            }
        }

        @ApiStatus.Internal
        public void updateNanoTickFromIntegratedServer(MinecraftServer server)
        {
            this.lastNanoTime = System.nanoTime();

            if (server != null)
            {
                this.measuredMSPT = MathUtils.average(server.getTickTimes()) / 1000000D;
                this.measuredTPS = this.measuredMSPT <= 50 ? this.tickRate : (1000D / this.measuredMSPT);
                this.actualTPS = (1000D / this.measuredMSPT);
                if (MaLiLibReference.DEBUG_MODE)
                {
                    this.calculateAverages();
                }
                this.isValid = true;
            }
        }

        /**
         * Update the direct-server data from Carpet into this Tick Data.
         * @param tps ()
         * @param mspt ()
         */
        public void updateNanoTickFromServerDirect(final double tps, final double mspt)
        {
            if (this.useDirectServerData && !this.hasServuxData)
            {
                // For things like Carpet
                this.directMSPT = mspt;
                this.directTPS = tps;
                this.lastDirectTick = System.nanoTime();
                if (MaLiLibReference.DEBUG_MODE)
                {
                    this.calculateAverages();
                }
                this.isValid = true;
            }
        }

        /**
         * Update the direct-server data from Servux into this Tick Data.
         * @param tps ()
         * @param mspt ()
         * @param sprintTicks ()
         * @param frozen ()
         * @param sprinting ()
         * @param stepping ()
         */
        public void updateNanoTickFromServux(final double tps,
                                             final double mspt,
                                             final long sprintTicks,
                                             boolean frozen,
                                             boolean sprinting,
                                             boolean stepping)
        {
            if (this.useDirectServerData)
            {
                // For Servux
                this.directMSPT = MathUtils.round(mspt, 2);
                this.directTPS = MathUtils.round(tps, 2);
                this.lastDirectTick = System.nanoTime();
                this.sprintTicks = sprintTicks;
                this.isFrozen = frozen;
                this.isSprinting = sprinting;
                this.isStepping = stepping;
                if (MaLiLibReference.DEBUG_MODE)
                {
                    this.calculateAverages();
                }
                this.hasServuxData = true;
                this.isValid = true;
            }
        }

        @ApiStatus.Internal
        private void calculateAverages()
        {
            if (this.lastMeasurementTick >= MAX_HISTORY)
            {
                this.lastMeasurementTick = 0;
            }

            this.prevMeasuredMSPT[this.lastMeasurementTick] = this.measuredMSPT;
            this.prevMeasuredTPS[this.lastMeasurementTick] = this.measuredTPS;
            this.avgMeasuredMSPT = MathUtils.average(this.prevMeasuredMSPT);
            this.avgMeasuredTPS = MathUtils.average(this.prevMeasuredTPS);
            this.lastMeasurementTick++;
        }

        /**
         * Return if the data has been updated, and is valid.
         * @return ()
         */
        public boolean isValid() { return this.isValid; }

        /**
         * Return if this data has been timed synced and estimated.
         * @return ()
         */
        public boolean hasTimeSynced() { return this.hasTimeSynced; }

        /**
         * Return if this data has been timed synced from a remote server.
         * @return ()
         */
        public boolean hasDirectData() { return this.useDirectServerData; }

        /**
         * Return if this data has been timed synced from a Servux server.
         * @return ()
         */
        public boolean hasServuxData() { return this.hasServuxData; }

        /**
         * Return the Vanilla Tick Rate.
         * @return ()
         */
        public double getTickRate() { return this.tickRate; }

        /**
         * Return the Servux sprintTicks.
         * @return ()
         */
        public long getSprintTicks() { return this.sprintTicks; }

        /**
         * Return the Servux Frozen status.
         * @return ()
         */
        public boolean isFrozen() { return this.isFrozen; }

        /**
         * Return the Servux Sprinting status.
         * @return ()
         */
        public boolean isSprinting() { return this.isSprinting; }

        /**
         * Return the Servux Stepping status.
         * @return ()
         */
        public boolean isStepping() { return this.isStepping; }

        /**
         * Return the Measured TPS that has been calculated.
         * @return ()
         */
        public double getMeasuredTPS() { return this.measuredTPS; }

        /**
         * Return the Measured MSPT that has been calculated.
         * @return ()
         */
        public double getMeasuredMSPT() { return this.measuredMSPT; }

        /**
         * Return the Direct TPS that has been synced from a remote server.
         * @return ()
         */
        public double getDirectTPS() { return this.directTPS; }

        /**
         * Return the Direct MSPT that has been synced from a remote server.
         * @return ()
         */
        public double getDirectMSPT() { return this.directMSPT; }

        /**
         * Return the Actual TPS that has been calculated (Non TickRate-adjusted) via flat math between update packets.
         * @return ()
         */
        public double getActualTPS() { return this.actualTPS; }

        @ApiStatus.Internal
        public double getAverageMSPT() { return this.avgMeasuredMSPT; }

        @ApiStatus.Internal
        public double getAverageTPS() { return this.avgMeasuredTPS; }
    }
}
