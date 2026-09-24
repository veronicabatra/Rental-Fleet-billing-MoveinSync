const API_BASE = "http://localhost:8080";


// =====================================================
// ELEMENTS
// =====================================================

const vehicleId =
    document.getElementById("vehicleId");

const billingMonth =
    document.getElementById("billingMonth");

const chargingModel =
    document.getElementById("chargingModel");

const pricingType =
    document.getElementById("pricingType");

const perKmSection =
    document.getElementById("perKmSection");

const perTripSection =
    document.getElementById("perTripSection");

const fixedMonthlySection =
    document.getElementById("fixedMonthlySection");

const flatPricingSection =
    document.getElementById("flatPricingSection");

const tieredPricingSection =
    document.getElementById("tieredPricingSection");

const rateChangeSection =
    document.getElementById("rateChangeSection");

const rateChange =
    document.getElementById("rateChange");

const pricingPeriodsSection =
    document.getElementById("pricingPeriodsSection");

const pricingPeriodsContainer =
    document.getElementById("pricingPeriodsContainer");

const addPricingPeriodBtn =
    document.getElementById("addPricingPeriodBtn");

const extraChargesSection =
    document.getElementById("extraChargesSection");

const tripSection =
    document.getElementById("tripSection");

const calculateSection =
    document.getElementById("calculateSection");

const slabsContainer =
    document.getElementById("slabsContainer");

const tripsContainer =
    document.getElementById("tripsContainer");

const resultSection =
    document.getElementById("resultSection");

const errorBox =
    document.getElementById("errorBox");


// =====================================================
// PAGE LOAD
// =====================================================

document.addEventListener(
    "DOMContentLoaded",
    async () => {

        const today = new Date();

        billingMonth.value =
            today.getFullYear() +
            "-" +
            String(today.getMonth() + 1)
                .padStart(2, "0");

        await loadVehicles();

        resetAllDynamicSections();

    }
);


// =====================================================
// LOAD VEHICLES
// =====================================================

async function loadVehicles() {

    try {

        const response =
            await fetch(
                `${API_BASE}/api/vehicles`
            );

        if (!response.ok) {

            throw new Error(
                "Unable to load vehicles."
            );

        }

        const vehicles =
            await response.json();

        vehicleId.innerHTML =
            `<option value="">
                Select Vehicle
             </option>`;

        vehicles.forEach(vehicle => {

            const option =
                document.createElement("option");

            option.value =
                vehicle.id;

            option.textContent =
                `${vehicle.registrationNumber} - ${vehicle.model}`;

            vehicleId.appendChild(option);

        });

    } catch (error) {

        showError(error.message);

    }
}


// =====================================================
// VEHICLE
// =====================================================

vehicleId.addEventListener(
    "change",
    () => {

        resetAfterVehicle();

        if (vehicleId.value) {

            chargingModel.disabled = false;

        } else {

            chargingModel.disabled = true;
            chargingModel.value = "";

        }

    }
);


// =====================================================
// CHARGING MODEL
// =====================================================

chargingModel.addEventListener(
    "change",
    handleChargingModel
);


function handleChargingModel() {

    hidePricingSections();

    hideFinalSections();

    const model =
        chargingModel.value;

    if (!model) {
        return;
    }


    // -----------------------------
    // PER KM
    // -----------------------------

    if (model === "PER_KM") {

        perKmSection.style.display =
            "block";

        rateChangeSection.style.display =
            "block";

        pricingType.value = "";

        flatPricingSection.style.display =
            "none";

        tieredPricingSection.style.display =
            "none";

    }


    // -----------------------------
    // PER TRIP
    // -----------------------------

    else if (model === "PER_TRIP") {

        perTripSection.style.display =
            "block";

        rateChangeSection.style.display =
            "block";

    }


    // -----------------------------
    // FIXED MONTHLY
    // -----------------------------

    else if (model === "FIXED_MONTHLY") {

        fixedMonthlySection.style.display =
            "block";

        rateChangeSection.style.display =
            "none";

        pricingPeriodsSection.style.display =
            "none";

        rateChange.checked = false;

        pricingPeriodsContainer.innerHTML =
            "";

    }

}


// =====================================================
// PRICING TYPE
// =====================================================

pricingType.addEventListener(
    "change",
    handlePricingType
);


function handlePricingType() {

    flatPricingSection.style.display =
        "none";

    tieredPricingSection.style.display =
        "none";


    if (
        pricingType.value ===
        "FLAT"
    ) {

        flatPricingSection.style.display =
            "block";

    }


    if (
        pricingType.value ===
        "TIERED"
    ) {

        tieredPricingSection.style.display =
            "block";


        if (
            slabsContainer.children.length === 0
        ) {

            addSlab();
            addSlab();

        }

    }

}


// =====================================================
// RATE CHANGE
// =====================================================

rateChange.addEventListener(
    "change",
    handleRateChange
);


function handleRateChange() {

    if (rateChange.checked) {

        pricingPeriodsSection.style.display =
            "block";

        if (
            pricingPeriodsContainer.children.length === 0
        ) {

            addPricingPeriod();

        }

    } else {

        pricingPeriodsSection.style.display =
            "none";

        pricingPeriodsContainer.innerHTML =
            "";

    }

    checkPricingCompletion();

}


// =====================================================
// ADD PRICING PERIOD
// =====================================================

addPricingPeriodBtn.addEventListener(
    "click",
    () => {

        addPricingPeriod();

    }
);


function addPricingPeriod() {

    const period =
        document.createElement("div");

    period.className =
        "pricing-period-card";


    period.innerHTML = `

        <div class="d-flex justify-content-between align-items-center mb-3">

            <h6 class="mb-0">
                Pricing Period
            </h6>

            <button
                type="button"
                class="btn btn-sm btn-outline-danger"
                onclick="
                    this.closest('.pricing-period-card').remove();
                    checkPricingCompletion();
                "
            >
                Remove
            </button>

        </div>


        <div class="row g-3">

            <div class="col-md-6">

                <label class="form-label">
                    From Date
                </label>

                <input
                    type="date"
                    class="form-control period-from"
                >

            </div>


            <div class="col-md-6">

                <label class="form-label">
                    To Date
                </label>

                <input
                    type="date"
                    class="form-control period-to"
                >

            </div>

        </div>


        <div
            class="period-flat-section mt-3"
            style="display:none;"
        >

            <label class="form-label">
                Rate Per KM (₹)
            </label>

            <input
                type="number"
                class="form-control period-per-km-rate"
                min="0"
                step="0.01"
                placeholder="50"
            >

        </div>


        <div
            class="period-trip-section mt-3"
            style="display:none;"
        >

            <label class="form-label">
                Rate Per Trip (₹)
            </label>

            <input
                type="number"
                class="form-control period-per-trip-rate"
                min="0"
                step="0.01"
                placeholder="500"
            >

        </div>


        <div
            class="period-tiered-section mt-3"
            style="display:none;"
        >

            <div class="d-flex justify-content-between align-items-center mb-2">

                <strong>
                    Period Slabs
                </strong>

                <button
                    type="button"
                    class="btn btn-sm btn-outline-primary period-add-slab"
                >
                    + Add Slab
                </button>

            </div>

            <div class="period-slabs-container"></div>

        </div>

    `;


    pricingPeriodsContainer.appendChild(
        period
    );


    setupPricingPeriod(
        period
    );

}


// =====================================================
// SETUP PRICING PERIOD
// =====================================================

function setupPricingPeriod(period) {

    const fromInput =
        period.querySelector(
            ".period-from"
        );

    const toInput =
        period.querySelector(
            ".period-to"
        );

    const addSlabButton =
        period.querySelector(
            ".period-add-slab"
        );


    fromInput.addEventListener(
        "change",
        checkPricingCompletion
    );

    toInput.addEventListener(
        "change",
        checkPricingCompletion
    );


    addSlabButton.addEventListener(
        "click",
        () => {

            addPeriodSlab(
                period
            );

        }
    );


    if (
        chargingModel.value ===
        "PER_KM"
    ) {

        if (
            pricingType.value ===
            "FLAT"
        ) {

            period.querySelector(
                ".period-flat-section"
            ).style.display =
                "block";

        }


        if (
            pricingType.value ===
            "TIERED"
        ) {

            period.querySelector(
                ".period-tiered-section"
            ).style.display =
                "block";

            const container =
                period.querySelector(
                    ".period-slabs-container"
                );

            if (
                container.children.length === 0
            ) {

                addPeriodSlab(period);
                addPeriodSlab(period);

            }

        }

    }


    if (
        chargingModel.value ===
        "PER_TRIP"
    ) {

        period.querySelector(
            ".period-trip-section"
        ).style.display =
            "block";

    }

}


// =====================================================
// ADD PERIOD SLAB
// =====================================================

function addPeriodSlab(period) {

    const container =
        period.querySelector(
            ".period-slabs-container"
        );


    const row =
        document.createElement("div");

    row.className =
        "period-slab-row";


    row.innerHTML = `

        <div class="row g-2 align-items-end">

            <div class="col-md-3">

                <label class="form-label">
                    Min KM
                </label>

                <input
                    type="number"
                    class="form-control period-slab-min"
                    min="0"
                    step="0.001"
                >

            </div>


            <div class="col-md-3">

                <label class="form-label">
                    Max KM
                </label>

                <input
                    type="number"
                    class="form-control period-slab-max"
                    min="0"
                    step="0.001"
                >

            </div>


            <div class="col-md-3">

                <label class="form-label">
                    Rate / KM
                </label>

                <input
                    type="number"
                    class="form-control period-slab-rate"
                    min="0"
                    step="0.01"
                >

            </div>


            <div class="col-md-3">

                <button
                    type="button"
                    class="btn btn-outline-danger w-100"
                >
                    Remove
                </button>

            </div>

        </div>

    `;


    row.querySelector(
        "button"
    ).addEventListener(
        "click",
        () => {

            row.remove();

            checkPricingCompletion();

        }
    );


    container.appendChild(row);

}


// =====================================================
// PRICING FIELD CHANGE
// =====================================================

document.addEventListener(
    "input",
    checkPricingCompletion
);

document.addEventListener(
    "change",
    checkPricingCompletion
);


function checkPricingCompletion() {

    if (!vehicleId.value) {
        return;
    }

    const model =
        chargingModel.value;


    if (model === "PER_KM") {

        if (
            pricingType.value ===
            "FLAT"
        ) {

            if (
                validNumber(
                    "perKmRate"
                )
            ) {

                showFinalSections();

            }

        }


        if (
            pricingType.value ===
            "TIERED"
        ) {

            if (validSlabs()) {

                showFinalSections();

            }

        }

    }


    if (model === "PER_TRIP") {

        if (
            validNumber(
                "perTripRate"
            )
        ) {

            showFinalSections();

        }

    }


    if (model === "FIXED_MONTHLY") {

        if (
            validNumber("monthlyFee") &&
            validNumber("includedKm") &&
            validNumber("overageRate")
        ) {

            showFinalSections();

        }

    }

}


// =====================================================
// SHOW FINAL SECTIONS
// =====================================================

function showFinalSections() {

    extraChargesSection.style.display =
        "block";

    tripSection.style.display =
        "block";

    calculateSection.style.display =
        "block";


    if (
        tripsContainer.children.length === 0
    ) {

        addTrip();

    }

}


// =====================================================
// ADD SLAB
// =====================================================

document
    .getElementById("addSlabBtn")
    .addEventListener(
        "click",
        addSlab
    );


function addSlab() {

    const row =
        document.createElement("div");

    row.className =
        "row g-2 slab-row";


    row.innerHTML = `

        <div class="col-md-3">

            <input
                type="number"
                class="form-control slab-min"
                placeholder="Min KM"
                min="0"
                step="0.001"
            >

        </div>


        <div class="col-md-3">

            <input
                type="number"
                class="form-control slab-max"
                placeholder="Max KM"
                min="0"
                step="0.001"
            >

        </div>


        <div class="col-md-3">

            <input
                type="number"
                class="form-control slab-rate"
                placeholder="Rate / KM"
                min="0"
                step="0.01"
            >

        </div>


        <div class="col-md-3">

            <button
                type="button"
                class="btn btn-outline-danger w-100"
            >
                Remove
            </button>

        </div>

    `;


    row.querySelector(
        "button"
    ).addEventListener(
        "click",
        () => {

            row.remove();

            checkPricingCompletion();

        }
    );


    slabsContainer.appendChild(row);

}


// =====================================================
// ADD TRIP
// =====================================================

document
    .getElementById("addTripBtn")
    .addEventListener(
        "click",
        addTrip
    );


function addTrip() {

    const trip =
        document.createElement("div");

    trip.className =
        "trip-card";


    trip.innerHTML = `

        <div class="d-flex justify-content-between mb-3">

            <strong>
                Trip
            </strong>

            <button
                type="button"
                class="btn btn-sm btn-outline-danger"
            >
                Remove
            </button>

        </div>


        <div class="row g-3">

            <div class="col-md-4">

                <label class="form-label">
                    Trip Date
                </label>

                <input
                    type="date"
                    class="form-control trip-date"
                    required
                >

            </div>


            <div class="col-md-4">

                <label class="form-label">
                    Distance (KM)
                </label>

                <input
                    type="number"
                    class="form-control trip-distance"
                    min="0"
                    step="0.001"
                    placeholder="120"
                    required
                >

            </div>


            <div class="col-md-4">

                <label class="form-label">
                    Dead KM
                </label>

                <input
                    type="number"
                    class="form-control trip-dead"
                    min="0"
                    step="0.001"
                    value="0"
                >

            </div>


            <div class="col-md-4">

                <label class="form-label">
                    Waiting Hours
                </label>

                <input
                    type="number"
                    class="form-control trip-waiting"
                    min="0"
                    step="0.001"
                    value="0"
                >

            </div>


            <div class="col-md-4">

                <label class="form-label">
                    Toll Amount (₹)
                </label>

                <input
                    type="number"
                    class="form-control trip-toll"
                    min="0"
                    step="0.01"
                    value="0"
                >

            </div>


            <div class="col-md-4 d-flex align-items-end">

                <div class="form-check mb-2">

                    <input
                        type="checkbox"
                        class="form-check-input trip-night"
                    >

                    <label class="form-check-label">
                        Night Trip
                    </label>

                </div>

            </div>

        </div>

    `;


    trip.querySelector(
        "button"
    ).addEventListener(
        "click",
        () => {

            trip.remove();

        }
    );


    tripsContainer.appendChild(trip);

}


// =====================================================
// VALIDATE SLABS
// =====================================================

function validSlabs() {

    const rows =
        document.querySelectorAll(
            ".slab-row"
        );


    if (rows.length === 0) {
        return false;
    }


    for (const row of rows) {

        const min =
            row.querySelector(
                ".slab-min"
            ).value;

        const max =
            row.querySelector(
                ".slab-max"
            ).value;

        const rate =
            row.querySelector(
                ".slab-rate"
            ).value;


        if (
            min === "" ||
            rate === ""
        ) {

            return false;

        }


        if (
            max !== "" &&
            Number(max) < Number(min)
        ) {

            return false;

        }

    }


    return true;

}


// =====================================================
// VALIDATE PRICING PERIODS
// =====================================================

function validatePricingPeriodsFrontend() {

    if (!rateChange.checked) {
        return true;
    }


    const periods =
        Array.from(
            document.querySelectorAll(
                ".pricing-period-card"
            )
        );


    if (periods.length === 0) {

        throw new Error(
            "Please add at least one pricing period."
        );

    }


    const monthValue =
        billingMonth.value;


    if (!monthValue) {

        throw new Error(
            "Please select billing month first."
        );

    }


    const monthStart =
        new Date(
            `${monthValue}-01T00:00:00`
        );


    const nextMonth =
        new Date(monthStart);

    nextMonth.setMonth(
        nextMonth.getMonth() + 1
    );


    const monthEnd =
        new Date(nextMonth);

    monthEnd.setDate(0);


    const data =
        periods
            .map(period => {

                const from =
                    period.querySelector(
                        ".period-from"
                    ).value;

                const to =
                    period.querySelector(
                        ".period-to"
                    ).value;

                if (!from || !to) {

                    throw new Error(
                        "Please enter From Date and To Date for every pricing period."
                    );

                }


                return {
                    element: period,
                    from: new Date(
                        `${from}T00:00:00`
                    ),
                    to: new Date(
                        `${to}T00:00:00`
                    )
                };

            })
            .sort(
                (a, b) =>
                    a.from - b.from
            );


    let expected =
        new Date(monthStart);


    for (const period of data) {

        if (
            period.from < monthStart ||
            period.to > monthEnd
        ) {

            throw new Error(
                "Pricing period must remain inside billing month."
            );

        }


        if (
            period.from > period.to
        ) {

            throw new Error(
                "Pricing period start date cannot be after end date."
            );

        }


        if (
            period.from.getTime() !==
            expected.getTime()
        ) {

            throw new Error(
                "Pricing periods must continuously cover the complete billing month without gaps or overlaps."
            );

        }


        const next =
            new Date(period.to);

        next.setDate(
            next.getDate() + 1
        );

        expected = next;


        const model =
            chargingModel.value;


        if (
            model === "PER_KM"
        ) {

            if (
                pricingType.value ===
                "FLAT"
            ) {

                const rate =
                    period.element.querySelector(
                        ".period-per-km-rate"
                    ).value;

                if (
                    rate === "" ||
                    Number(rate) <= 0
                ) {

                    throw new Error(
                        "Every pricing period needs a valid Per KM rate."
                    );

                }

            }


            if (
                pricingType.value ===
                "TIERED"
            ) {

                const rows =
                    period.element.querySelectorAll(
                        ".period-slab-row"
                    );


                if (rows.length === 0) {

                    throw new Error(
                        "Every pricing period needs at least one slab."
                    );

                }


                for (const row of rows) {

                    const min =
                        row.querySelector(
                            ".period-slab-min"
                        ).value;

                    const max =
                        row.querySelector(
                            ".period-slab-max"
                        ).value;

                    const rate =
                        row.querySelector(
                            ".period-slab-rate"
                        ).value;


                    if (
                        min === "" ||
                        rate === ""
                    ) {

                        throw new Error(
                            "Every pricing period slab needs Min KM and Rate."
                        );

                    }


                    if (
                        max !== "" &&
                        Number(max) < Number(min)
                    ) {

                        throw new Error(
                            "Slab Max KM cannot be less than Min KM."
                        );

                    }

                }

            }

        }


        if (
            model === "PER_TRIP"
        ) {

            const rate =
                period.element.querySelector(
                    ".period-per-trip-rate"
                ).value;

            if (
                rate === "" ||
                Number(rate) <= 0
            ) {

                throw new Error(
                    "Every pricing period needs a valid Per Trip rate."
                );

            }

        }

    }


    if (
        expected.getTime() !==
        new Date(nextMonth).getTime()
    ) {

        throw new Error(
            "Pricing periods must cover the complete billing month."
        );

    }

}


// =====================================================
// COLLECT PRICING PERIODS
// =====================================================

function collectPricingPeriods() {

    if (!rateChange.checked) {
        return [];
    }


    validatePricingPeriodsFrontend();


    const cards =
        document.querySelectorAll(
            ".pricing-period-card"
        );


    return Array.from(cards)
        .map(card => {

            const period = {

                fromDate:
                    card.querySelector(
                        ".period-from"
                    ).value,

                toDate:
                    card.querySelector(
                        ".period-to"
                    ).value,

                perKmRate:
                    null,

                perTripRate:
                    null,

                monthlyFee:
                    null,

                includedKm:
                    null,

                overageRate:
                    null,

                slabs:
                    []

            };


            if (
                chargingModel.value ===
                "PER_KM"
            ) {

                if (
                    pricingType.value ===
                    "FLAT"
                ) {

                    period.perKmRate =
                        getElementNumber(
                            card,
                            ".period-per-km-rate"
                        );

                }


                if (
                    pricingType.value ===
                    "TIERED"
                ) {

                    period.slabs =
                        Array.from(
                            card.querySelectorAll(
                                ".period-slab-row"
                            )
                        )
                        .map(row => {

                            const min =
                                row.querySelector(
                                    ".period-slab-min"
                                ).value;

                            const max =
                                row.querySelector(
                                    ".period-slab-max"
                                ).value;

                            const rate =
                                row.querySelector(
                                    ".period-slab-rate"
                                ).value;

                            return {

                                minKm:
                                    Number(min),

                                maxKm:
                                    max === ""
                                        ? null
                                        : Number(max),

                                ratePerKm:
                                    Number(rate)

                            };

                        });

                }

            }


            if (
                chargingModel.value ===
                "PER_TRIP"
            ) {

                period.perTripRate =
                    getElementNumber(
                        card,
                        ".period-per-trip-rate"
                    );

            }


            return period;

        });

}


// =====================================================
// COLLECT SLABS
// =====================================================

function collectSlabs() {

    const rows =
        document.querySelectorAll(
            ".slab-row"
        );


    return Array.from(rows)
        .map(row => {

            const min =
                row.querySelector(
                    ".slab-min"
                ).value;

            const max =
                row.querySelector(
                    ".slab-max"
                ).value;

            const rate =
                row.querySelector(
                    ".slab-rate"
                ).value;


            return {

                minKm:
                    Number(min),

                maxKm:
                    max === ""
                        ? null
                        : Number(max),

                ratePerKm:
                    Number(rate)

            };

        });

}


// =====================================================
// COLLECT TRIPS
// =====================================================

function collectTrips() {

    const cards =
        document.querySelectorAll(
            ".trip-card"
        );


    if (cards.length === 0) {

        throw new Error(
            "Please add at least one trip."
        );

    }


    return Array.from(cards)
        .map(card => {

            const date =
                card.querySelector(
                    ".trip-date"
                ).value;

            const distance =
                card.querySelector(
                    ".trip-distance"
                ).value;


            if (!date) {

                throw new Error(
                    "Please enter trip date."
                );

            }


            if (
                !distance ||
                Number(distance) <= 0
            ) {

                throw new Error(
                    "Distance must be greater than 0."
                );

            }


            return {

                tripDate:
                    date,

                distanceKm:
                    Number(distance),

                deadKm:
                    Number(
                        card.querySelector(
                            ".trip-dead"
                        ).value || 0
                    ),

                nightTrip:
                    card.querySelector(
                        ".trip-night"
                    ).checked,

                waitingHours:
                    Number(
                        card.querySelector(
                            ".trip-waiting"
                        ).value || 0
                    ),

                tollAmount:
                    Number(
                        card.querySelector(
                            ".trip-toll"
                        ).value || 0
                    )

            };

        });

}


// =====================================================
// BUILD REQUEST
// =====================================================

function buildPayload() {

    const model =
        chargingModel.value;


    const payload = {

        vehicleId:
            Number(
                vehicleId.value
            ),

        month:
            billingMonth.value,

        chargingModel:
            model,

        pricingType:
            model === "PER_KM"
                ? pricingType.value
                : null,

        perKmRate:
            model === "PER_KM" &&
            pricingType.value === "FLAT"
                ? getNumber("perKmRate")
                : null,

        perTripRate:
            model === "PER_TRIP"
                ? getNumber("perTripRate")
                : null,

        monthlyFee:
            model === "FIXED_MONTHLY"
                ? getNumber("monthlyFee")
                : null,

        includedKm:
            model === "FIXED_MONTHLY"
                ? getNumber("includedKm")
                : null,

        overageRate:
            model === "FIXED_MONTHLY"
                ? getNumber("overageRate")
                : null,

        nightChargePerTrip:
            getNumber(
                "nightChargePerTrip"
            ),

        waitingChargePerHour:
            getNumber(
                "waitingChargePerHour"
            ),

        tollMode:
            document.getElementById(
                "tollMode"
            ).value,

        slabs:
            model === "PER_KM" &&
            pricingType.value === "TIERED"
                ? collectSlabs()
                : [],

        rateChange:
            rateChange.checked,

        pricingPeriods:
            collectPricingPeriods(),

        trips:
            collectTrips()

    };


    return payload;

}


// =====================================================
// CALCULATE
// =====================================================

document
    .getElementById("calculateBtn")
    .addEventListener(
        "click",
        calculateBill
    );


async function calculateBill() {

    hideError();


    try {

        const payload =
            buildPayload();


        console.log(
            "Billing request:",
            payload
        );


        const button =
            document.getElementById(
                "calculateBtn"
            );


        button.disabled = true;

        button.textContent =
            "Calculating...";


        const response =
            await fetch(
                `${API_BASE}/api/billing/run`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(
                            payload
                        )
                }
            );


        const text =
            await response.text();


        let data;


        try {

            data =
                text
                    ? JSON.parse(text)
                    : {};

        } catch {

            data = {
                message: text
            };

        }


        if (!response.ok) {

            throw new Error(
                data.message ||
                data.error ||
                `Server error: ${response.status}`
            );

        }


        displayResult(data);


    } catch (error) {

        console.error(error);

        showError(
            error.message
        );


    } finally {

        const button =
            document.getElementById(
                "calculateBtn"
            );

        button.disabled = false;

        button.textContent =
            "Calculate Bill";

    }

}


// =====================================================
// RESULT
// =====================================================

function displayResult(data) {

    resultSection.style.display =
        "block";


    document.getElementById(
        "totalAmount"
    ).textContent =
        money(data.totalAmount);


    document.getElementById(
        "tripCount"
    ).textContent =
        data.tripCount || 0;


    document.getElementById(
        "totalDutyKm"
    ).textContent =
        data.totalDutyKm || 0;


    document.getElementById(
        "totalDeadKm"
    ).textContent =
        data.totalDeadKm || 0;


    const tbody =
        document.getElementById(
            "tripBillsBody"
        );


    tbody.innerHTML = "";


    (data.tripBills || [])
        .forEach(trip => {

            const row =
                document.createElement("tr");


            const distance =
                trip.dutyKm ??
                trip.distanceKm ??
                0;


            row.innerHTML = `

                <td>
                    ${trip.tripId ?? "-"}
                </td>

                <td>
                    ${trip.tripDate ?? "-"}
                </td>

                <td>
                    ${distance}
                </td>

                <td>
                    ${trip.deadKm ?? 0}
                </td>

                <td>
                    ${money(trip.baseCharge)}
                </td>

                <td>
                    ${money(trip.fixedAllocation)}
                </td>

                <td>
                    ${money(trip.nightCharge)}
                </td>

                <td>
                    ${money(trip.waitingCharge)}
                </td>

                <td>
                    ${money(trip.tollCharge)}
                </td>

                <td>
                    <strong>
                        ${money(trip.totalAmount)}
                    </strong>
                </td>

            `;


            tbody.appendChild(row);

        });


    resultSection.scrollIntoView({
        behavior: "smooth"
    });

}


// =====================================================
// RESET
// =====================================================

function resetAfterVehicle() {

    hidePricingSections();

    hideFinalSections();

    pricingType.value = "";

    slabsContainer.innerHTML = "";

    pricingPeriodsContainer.innerHTML = "";

    tripsContainer.innerHTML = "";

    rateChange.checked = false;

}


function resetAllDynamicSections() {

    hidePricingSections();

    hideFinalSections();

    pricingPeriodsSection.style.display =
        "none";

    rateChangeSection.style.display =
        "none";

    chargingModel.disabled =
        !vehicleId.value;

}


function hidePricingSections() {

    perKmSection.style.display =
        "none";

    perTripSection.style.display =
        "none";

    fixedMonthlySection.style.display =
        "none";

    flatPricingSection.style.display =
        "none";

    tieredPricingSection.style.display =
        "none";

}


function hideFinalSections() {

    extraChargesSection.style.display =
        "none";

    tripSection.style.display =
        "none";

    calculateSection.style.display =
        "none";

}


// =====================================================
// HELPERS
// =====================================================

function validNumber(id) {

    const element =
        document.getElementById(id);


    return (
        element &&
        element.value !== "" &&
        Number(element.value) >= 0
    );

}


function getNumber(id) {

    const element =
        document.getElementById(id);


    if (
        !element ||
        element.value === ""
    ) {

        return null;

    }


    return Number(
        element.value
    );

}


function getElementNumber(
    parent,
    selector
) {

    const element =
        parent.querySelector(
            selector
        );


    if (
        !element ||
        element.value === ""
    ) {

        return null;

    }


    return Number(
        element.value
    );

}


function money(value) {

    return "₹" +
        Number(
            value || 0
        ).toFixed(2);

}


function showError(message) {

    errorBox.textContent =
        message;

    errorBox.style.display =
        "block";

}


function hideError() {

    errorBox.textContent =
        "";

    errorBox.style.display =
        "none";

}