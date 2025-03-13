import type * as Preset from "@docusaurus/preset-classic";
import type { Config } from "@docusaurus/types";
import { themes as prismThemes } from "prism-react-renderer";

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

const config: Config = {
    title: "MineStamp Documentation",
    favicon: "img/favicon.ico",
    staticDirectories: ["static"],
    trailingSlash: true,

    url: "https://mine-stamp.plugin.nikomaru.dev",
    baseUrl: "/",

    organizationName: "morinoparty",
    projectName: "MineStamp",

    onBrokenLinks: "throw",
    onBrokenMarkdownLinks: "warn",

    i18n: {
        defaultLocale: "en",
        locales: ["en","ja"],
        localeConfigs: {
            en: {
                label: 'English',
            },
            ja: {
                label: '日本語',
            },
        },
    },

    presets: [
        [
            "classic",
            {
                docs: {
                    sidebarPath: "./sidebars.ts",
                    routeBasePath: "/",
                    editUrl:
                        "https://github.com/nlkomaru/minecraftpluginmanager/tree/master/docs/",
                },
                theme: {
                    customCss: "./src/css/custom.css",
                },
            } satisfies Preset.Options,
        ],
    ],
    plugins: [
        ["./src/plugins/tailwind-config.js", {}],
        ["./src/plugins/llms-txt.ts", {}],
        [
            require.resolve("@easyops-cn/docusaurus-search-local"),
            {
                indexDocs: true,
                language: ["en", "ja"],
                docsRouteBasePath: "/",
            },
        ],
    ],
    themeConfig: {
        image: "img/docusaurus-social-card.jpg",
        navbar: {
            title: "MineStamp",
            logo: {
                alt: "MineStamp Logo",
                src: "img/logo.svg",
            },
            items: [
                {
                    href: "https://github.com/nlkomaru/MineStamp",
                    label: "GitHub",
                    position: "right",
                },
                {
                    href: "https://modrinth.com/plugin/MineStamp",
                    label: "Download",
                    position: "right",
                },
                {
                    type: 'localeDropdown',
                    position: 'right',
                },
            ],
        },
        footer: {
            style: "dark",
            links: [
                {
                    title: "Documentation",
                    items: [
                        {
                            label: "Introduction",
                            to: "/intro",
                        },
                    ],
                },
                {
                    title: "Community",
                    items: [
                        {
                            label: "Homepage",
                            href: "https://morino.party",
                        },
                        {
                            label: "Discord",
                            href: "https://discord.com/invite/9HdanPM",
                        },
                        {
                            label: "X",
                            href: "https://x.com/morinoparty",
                        },
                    ],
                },
                {
                    title: "Other",
                    items: [
                        {
                            label: "GitHub",
                            href: "https://github.com/nlkomaru/minecraftpluginmanager",
                        },
                    ],
                },
            ],
            copyright: `No right reserved. This docs under CC0. Built with Docusaurus.`,
        },
        prism: {
            additionalLanguages: [
                "java",
                "groovy",
                "diff",
                "toml",
                "yaml",
                "kotlin",
            ],
            theme: prismThemes.github,
            darkTheme: prismThemes.dracula,
        },
    } satisfies Preset.ThemeConfig,
};

export default config;
