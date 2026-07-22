// swift-tools-version: 5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "vonage_voice",
    platforms: [
        // iOS 15 is required by VonageClientSDKVoice, whose Swift Package
        // declares .iOS(.v15). The podspec is kept in lockstep at iOS 15.
        .iOS(.v15)
    ],
    products: [
        // Library name uses hyphens (Flutter convention); the target keeps the
        // underscore plugin name so registration resolves VonageVoicePlugin.
        .library(name: "vonage-voice", targets: ["vonage_voice"])
    ],
    dependencies: [
        // Flutter injects this package into the build in SPM mode.
        .package(name: "FlutterFramework", path: "../FlutterFramework"),
        // Official Vonage Client SDK Swift Package. Kept semver-aligned with the
        // podspec constraint (~> 2.3) so CocoaPods and SPM resolve the same SDK.
        .package(
            url: "https://github.com/Vonage/vonage-client-sdk-ios",
            from: "2.3.0"
        )
    ],
    targets: [
        .target(
            name: "vonage_voice",
            dependencies: [
                .product(name: "FlutterFramework", package: "FlutterFramework"),
                .product(name: "VonageClientSDKVoice", package: "vonage-client-sdk-ios")
            ],
            resources: [
                // Apple privacy manifest — bundled so App Store privacy scanning
                // finds it under the SPM integration path.
                .process("PrivacyInfo.xcprivacy")
            ]
        )
    ]
)
